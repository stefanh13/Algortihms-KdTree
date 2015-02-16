package s3;


/*************************************************************************
 *************************************************************************/


import java.util.Arrays;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.StdRandom;


public class KdTree 
{
	
	private static final boolean RED   = true;	//Boolean value that stands for comparing points by x-values
    private static final boolean BLUE = false;	//Boolean value that stands for comparing points by y-values.
	private Node root;             				// root of BST

    private class Node 
    {
        private Point2D p;         // associated data
        private Node left, right;  // left and right subtrees
        private boolean color;
        private RectHV rect;

        public Node(Point2D p, boolean color, RectHV rect) 
        {
            this.p = p;
            this.color = color;
            this.rect = rect;
            this.left = null;
            this.right = null;
            
        }
    }
	
	// construct an empty set of points
    public KdTree() 
    {
    	root = null;
    }

    // is the set empty?
    public boolean isEmpty() 
    {
        return root == null;
    }

    // number of points in the set
    public int size()
    {
        return size(root);
    }
    
    private int size(Node node)
    {
    	if(node == null)
    	{
    		return 0;
    	}
    	
    	return 1 + size(node.left) + size(node.right);
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) 
    {
    	if(isEmpty())
    	{
    		RectHV rootRect = new RectHV(0, 0, 1, 1);
    		Node node = new Node(p, RED, rootRect);
    		root = node;
    		return;
    	}
    	//Calling helper function
    	insert(p, root);
    };
    
    //Helper function for insert
    private void insert(Point2D insertPoint, Node node)
    {
    		//Null check
    		if(node == null)
    		{
    			return;
    		}
    		//If point is already in the set then don't add it to the set.
    		if(insertPoint.equals(node.p))
    		{
    			return;
    		}
    		//If comparing by x values.
    		if(node.color == RED)
    		{
    			
    			if(node.p.x() > insertPoint.x())
    			{
    				insert(insertPoint, node.left);
    			}
    			else
    			{
    				insert(insertPoint, node.right);
    			}
    			
    		}
    		//If comparing by y values
    		else
    		{
    			
    			if(node.p.y() > insertPoint.y())
    			{
    				insert(insertPoint, node.left);
    				
    			}
    			else
    			{
    				insert(insertPoint, node.right);
    			}
    		
    		}
    		//If inserting by x values
    		if(node.color == RED)
    		{
    			if(node.p.x() > insertPoint.x())
    			{
    				//Check if I am rewinding the recursive call so I don't loose nodes.
    				if(node.left != null) return;
    				//Create rectangle relative to node
    				RectHV rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
    				node.left = new Node(insertPoint, BLUE, rect);
    				
    			}
    			else
    			{
    				//Check if I am rewinding the recursive call so I don't loose nodes.
    				if(node.right != null) return;
    				//Create rectangle relative to node
    				RectHV rect = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
    				node.right = new Node(insertPoint, BLUE, rect);
    			}
    		}
    		else
    		{
    			if(node.p.y() > insertPoint.y())
        		{
    				//Check if I am rewinding the recursive call so I don't loose nodes.
    				if(node.left != null) return;
    				//Create rectangle relative to node
        			RectHV rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
    				node.left = new Node(insertPoint, RED, rect);
        		}
    			else
    			{
    				//Check if I am rewinding the recursive call so I don't loose nodes.
    				if(node.right != null) return;
    				//Create rectangle relative to node
    				RectHV rect = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
    				node.right = new Node(insertPoint, RED, rect);
    			}
    		}
    		
	}

    // does the set contain the point p?
    public boolean contains(Point2D p) 
    {
        //Calling helper function
    	return contains(p, root);
    }
    
    //Helper function for contains
    private boolean contains(Point2D p, Node node)
    {
    	//Null check
    	if(node == null)
    	{
    		return false;
    	}
    	
    	if(node.p.equals(p))
    	{
    		return true;
    	}
    	
    	//Variables to remember the return boolean values when rewinding the recursion call.
    	boolean left = false;
    	boolean right = false;
    	
    	//If comparing by x-values
    	if(node.color == RED)
    	{
    		if(node.p.x() > p.x())
    		{
    			left = contains(p, node.left);
    		}
    		else
    		{
    			right = contains(p, node.right);
    		}
    	}
    	//If comparing by y-values
    	else
    	{
    		if(node.p.y() > p.y())
    		{
    			left = contains(p, node.left);
    		}
    		else
    		{
    			right = contains(p, node.right);
    		}
    	}
    	//Return true if left or right subtree contains the point.
    	return left || right;
    }

    // draw all of the points to standard draw
    public void draw() 
    {
    	StdDraw.square(0.5, 0.5, 0.5);
    	draw(root);
    }
    
    private void draw(Node node)
    {
    	if(node == null)
    	{
    		return;
    	}
    	
    	StdDraw.setPenRadius(0.008);
    	StdDraw.setPenColor(StdDraw.BLACK);
    	StdDraw.point(node.p.x(), node.p.y());
    	draw(node.left);
    	draw(node.right);
    	
    	if(node.color == RED)
    	{
    		StdDraw.setPenRadius(0.002);
    		StdDraw.setPenColor(StdDraw.RED);
    		StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
    	}
    	else
    	{
    		StdDraw.setPenRadius(0.002);
    		StdDraw.setPenColor(StdDraw.BLUE);
    		StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
    	}
    	
    	
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        //pointSet will be sent into the range helper function and points will be added if they are inside rect.
    	//This works because SET is call by reference.
    	SET<Point2D> pointSet = new SET<Point2D>();
    	//Calling helper function
    	range(rect, root, pointSet);
        
    	return pointSet;
    }
    //Helper function for range that will add points into pointSet.
    private void range(RectHV rect, Node node, SET<Point2D> pointSet)
    {
    	draw();
    	//Null check.
    	if(node == null)
    	{
    		return;
    	}
    	
    	
    	//If the rectangle of the node doesn't intersect with rect, then it is not necessary check it's subtrees.
    	if(!node.rect.intersects(rect))
    	{
    		return;
    	}
    	else
    	{
    		//If the point is in the rectangle then add it to pointSet.
    		if(rect.contains(node.p))
    		{
    			pointSet.add(node.p);
    		}
    		
    	}
    	
    	//Call recursively to node's subtrees.
    	range(rect, node.left, pointSet);
    	range(rect, node.right, pointSet);
    	
     }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        
    	if(isEmpty())
    	{
    		return null;
    	}
    	//Calling helper function.
    	return nearest(p, root, root.p);
    }
    
    //Helper function for nearest
    private Point2D nearest(Point2D p, Node node, Point2D nearest)
    {
    	//Null check
    	if(node == null)
    	{
    		return nearest;
    	}
    	
    	//To reduce distanceSquaredTo function calling.
    	double nearestDistance = nearest.distanceSquaredTo(p);
    	
    	//If point of the node is closer to p then the nearest point, update nearest point.
    	if(node.p.distanceSquaredTo(p) < nearestDistance)
    	{
    		nearest = node.p;
    	}
    	//Check of left child is null so I don't check the left child of null.
    	if(node.left != null)
    	{
    		//If the rectangle of the left child is closer to point p then the nearest point.
    		if(node.left.rect.distanceSquaredTo(p) < nearestDistance)
        	{
        		//Update nearest to the nearest point to p in the left subtree
    			nearest = nearest(p, node.left, nearest);
        	}
    	}
    	//Check of right child is null so I don't check the left child of null.
    	if(node.right != null)
    	{	
    		//If the rectangle of the right child is closer to point p then the nearest point.
    		if(node.right.rect.distanceSquaredTo(p) < nearestDistance)
        	{
    			//Update nearest to the nearest point to p in the right subtree
        		nearest = nearest(p, node.right, nearest);
        	}
    	}
    	
    	
    	return nearest;
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        /*int nrOfRecangles = in.readInt();
        int nrOfPointsCont = in.readInt();
        int nrOfPointsNear = in.readInt();
        RectHV[] rectangles = new RectHV[nrOfRecangles];
        Point2D[] pointsCont = new Point2D[nrOfPointsCont];
        Point2D[] pointsNear = new Point2D[nrOfPointsNear];
        for (int i = 0; i < nrOfRecangles; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsCont; i++) {
            pointsCont[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsNear; i++) {
            pointsNear[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        KdTree set = new KdTree();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble(), y = in.readDouble();
            set.insert(new Point2D(x, y));
        }
        for (int i = 0; i < nrOfRecangles; i++) {
            // Query on rectangle i, sort the result, and print
            Iterable<Point2D> ptset = set.range(rectangles[i]);
            int ptcount = 0;
            for (Point2D p : ptset)
                ptcount++;
            Point2D[] ptarr = new Point2D[ptcount];
            int j = 0;
            for (Point2D p : ptset) {
                ptarr[j] = p;
                j++;
            }
            Arrays.sort(ptarr);
            out.println("Inside rectangle " + (i + 1) + ":");
            for (j = 0; j < ptcount; j++)
                out.println(ptarr[j]);
        }
        out.println("Contain test:");
        for (int i = 0; i < nrOfPointsCont; i++) {
            out.println((i + 1) + ": " + set.contains(pointsCont[i]));
        }

        out.println("Nearest test:");
        for (int i = 0; i < nrOfPointsNear; i++) {
            out.println((i + 1) + ": " + set.nearest(pointsNear[i]));
        }
        out.println();*/

        KdTree set = new KdTree();
        for(int i = 0; i < 10000; i++)
        {
        	set.insert(new Point2D(StdIn.readDouble(), StdIn.readDouble()));
        }
        
        set.draw();
            
    }
}

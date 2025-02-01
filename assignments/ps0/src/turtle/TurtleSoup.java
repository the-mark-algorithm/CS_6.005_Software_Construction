/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package turtle;

import java.util.List;
import java.util.ArrayList;

public class TurtleSoup {

    /**
     * Draw a square.
     * 
     * @param turtle the turtle context
     * @param sideLength length of each side
     */
    public static void drawSquare(Turtle turtle, int sideLength) {
        for (int i = 0; i < 4; i++) {
            turtle.forward(sideLength);
            turtle.turn(90);
        }
    }

    /**
     * Determine inside angles of a regular polygon.
     * 
     * There is a simple formula for calculating the inside angles of a polygon;
     * you should derive it and use it here.
     * 
     * @param sides number of sides, where sides must be > 2
     * @return angle in degrees, where 0 <= angle < 360
     */
    public static double calculateRegularPolygonAngle(int sides) {
        return (180.0 * (sides - 2)) / sides;
    }

    /**
     * Determine number of sides given the size of interior angles of a regular polygon.
     * 
     * There is a simple formula for this; you should derive it and use it here.
     * Make sure you *properly round* the answer before you return it (see java.lang.Math).
     * HINT: it is easier if you think about the exterior angles.
     * 
     * @param angle size of interior angles in degrees, where 0 < angle < 180
     * @return the integer number of sides
     */
    public static int calculatePolygonSidesFromAngle(double angle) {
        return (int) Math.round((360 / (180 - angle)));
    }

    /**
     * Given the number of sides, draw a regular polygon.
     * 
     * (0,0) is the lower-left corner of the polygon; use only right-hand turns to draw.
     * 
     * @param turtle the turtle context
     * @param sides number of sides of the polygon to draw
     * @param sideLength length of each side
     */
    public static void drawRegularPolygon(Turtle turtle, int sides, int sideLength) {
        double turnAngle = 180 - calculateRegularPolygonAngle(sides);

        System.out.println(String.format("Turn Angle: %.2f", turnAngle));
        
        for (int i = 0; i < sides; i++) {
            turtle.forward(sideLength);
            turtle.turn(turnAngle);
        }
    }

    /**
     * Given the current direction, current location, and a target location, calculate the heading
     * towards the target point.
     * 
     * The return value is the angle input to turn() that would point the turtle in the direction of
     * the target point (targetX,targetY), given that the turtle is already at the point
     * (currentX,currentY) and is facing at angle currentHeading. The angle must be expressed in
     * degrees, where 0 <= angle < 360. 
     *
     * HINT: look at http://en.wikipedia.org/wiki/Atan2 and Java's math libraries
     * 
     * @param currentHeading current direction as clockwise from north
     * @param currentX current location x-coordinate
     * @param currentY current location y-coordinate
     * @param targetX target point x-coordinate
     * @param targetY target point y-coordinate
     * @return adjustment to heading (right turn amount) to get to target point,
     *         must be 0 <= angle < 360
     */
    public static double calculateHeadingToPoint(double currentHeading, int currentX, int currentY,
                                                 int targetX, int targetY) {
        
        double adjustmentAngle = 0.0;

        // 1. Center points at origin
        int centeredX = targetX - currentX;
        int centeredY = targetY - currentY;

        // 2. Check for same x or y coords!
        if (centeredX == 0) {
            if (centeredY > 0) {
                adjustmentAngle = 90.0;
            } else if (centeredY < 0) {
                adjustmentAngle = 270.0;
            }
        }
        
        if (centeredY == 0) {            
            if (centeredX > 0) {
                adjustmentAngle = 0.0;
            } else if (centeredX < 0) {
                adjustmentAngle = 180.0;
            }
        }

        if (centeredX != 0 && centeredY != 0) {
            // 3. Check which "quadrant" desination point is at in reference to current
            int quadrant = 1;

            if (centeredX > 0) {
                quadrant = centeredY > 0 ? 1 : 4;
            } else if (centeredX < 0) {
                quadrant = centeredY > 0? 2 : 3;
            }

            // 4. If destination point is in quadrant 2 or 3 -> rotate by -180 to move to quadrant 1 or 4
            if (quadrant == 2 || quadrant == 3) {
                centeredX = -centeredX;
                centeredY = -centeredY;
            }

            // 5. If coordinated differ calculate tan of (shifted) point
            double tangent = (double)centeredY / centeredX;

            // 6. Calculate arctan of tan to get angle within (-pi/2, pi/2)
            double arcTan = Math.atan(tangent) * (360 / (2 * Math.PI));

            // 7. Shift angle result by +180 clockwise if original point in Q2 or Q3, 
            // Shift by 360 if in Q4 -> means arcTan is < 0 (clockwise direction angle) want counter-clockwise direction angle
            adjustmentAngle = (quadrant == 2 || quadrant == 3) 
                ? arcTan + 180 
                : (quadrant == 4) 
                    ? arcTan + 360 
                    : arcTan; 
        }

        // 8. Adjust angle for initial vertical direction and current heading
        adjustmentAngle = (adjustmentAngle - 90 + currentHeading) % 360;


        // 9. Get clockwise adjustment angle
        adjustmentAngle = (360 - adjustmentAngle) % 360;

        return adjustmentAngle;
    }

    /**
     * Given a sequence of points, calculate the heading adjustments needed to get from each point
     * to the next.
     * 
     * Assumes that the turtle starts at the first point given, facing up (i.e. 0 degrees).
     * For each subsequent point, assumes that the turtle is still facing in the direction it was
     * facing when it moved to the previous point.
     * You should use calculateHeadingToPoint() to implement this function.
     * 
     * @param xCoords list of x-coordinates (must be same length as yCoords)
     * @param yCoords list of y-coordinates (must be same length as xCoords)
     * @return list of heading adjustments between points, of size 0 if (# of points) == 0,
     *         otherwise of size (# of points) - 1
     */
    public static List<Double> calculateHeadings(List<Integer> xCoords, List<Integer> yCoords) {

        ArrayList<Double> headingAdjustments = new ArrayList<>();
        double currentHeading = 0.0;

        for (int i = 0; i < xCoords.size() - 1; i++) {

            int currentX = xCoords.get(i);
            int currentY = yCoords.get(i);

            int nextX = xCoords.get(i + 1);
            int nextY = yCoords.get(i + 1);

            double turnAngle = calculateHeadingToPoint(currentHeading, currentX, currentY, nextX, nextY);

            headingAdjustments.add(turnAngle);

            // System.out.println(String.format("Current Point: (%d, %d) -> Next Point: (%d, %d), Current Heading: %f, Turn Angle: %f", currentX, currentY, nextX, nextY, currentHeading, headingAdjustments.get(i)));

            currentHeading = (currentHeading + turnAngle) % 360;
        }

        return headingAdjustments;
    }

    /**
     * Draw your personal, custom art.
     * 
     * Many interesting images can be drawn using the simple implementation of a turtle.  For this
     * function, draw something interesting; the complexity can be as little or as much as you want.
     * 
     * @param turtle the turtle context
     */
    public static void drawPersonalArt(Turtle turtle) {
        ArrayList<Integer> xCoords = new ArrayList<>();
        ArrayList<Integer> yCoords = new ArrayList<>();
        ArrayList<Integer> unitsForward = new ArrayList<>();


        xCoords.add(0);
        xCoords.add(-6);
        xCoords.add(-6);
        xCoords.add(-4);
        xCoords.add(-2);
        xCoords.add(-2);
        xCoords.add(-0);
        xCoords.add(1);
        xCoords.add(-1);
        xCoords.add(1);
        xCoords.add(2);
        xCoords.add(2);
        xCoords.add(4);
        xCoords.add(2);
        xCoords.add(4);
        xCoords.add(4);
        xCoords.add(4);
        xCoords.add(6);
        xCoords.add(4);
        xCoords.add(6);

        yCoords.add(0);
        yCoords.add(0);
        yCoords.add(4);
        yCoords.add(2);
        yCoords.add(4);
        yCoords.add(0);
        yCoords.add(4);
        yCoords.add(2);
        yCoords.add(2);
        yCoords.add(2);
        yCoords.add(0);
        yCoords.add(4);
        yCoords.add(3);
        yCoords.add(2);
        yCoords.add(0);
        yCoords.add(4);
        yCoords.add(2);
        yCoords.add(4);
        yCoords.add(2);
        yCoords.add(0);

        unitsForward.add(150);
        unitsForward.add(100);
        unitsForward.add(((int)Math.round(50 * Math.sqrt(2))));
        unitsForward.add(((int)Math.round(50 * Math.sqrt(2))));
        unitsForward.add(100);
        unitsForward.add(((int)Math.round(50 * Math.sqrt(5))));
        unitsForward.add(((int)Math.round(25 * Math.sqrt(5))));
        unitsForward.add(50);
        unitsForward.add(50);
        unitsForward.add((int)Math.round(25 * Math.sqrt(5)));
        unitsForward.add(100);
        unitsForward.add((int)Math.round(25 * Math.sqrt(5)));
        unitsForward.add((int)Math.round(25 * Math.sqrt(5)));
        unitsForward.add((int)Math.round(50 * Math.sqrt(2)));
        unitsForward.add(100);
        unitsForward.add(50);
        unitsForward.add((int)Math.round(50 * Math.sqrt(2)));
        unitsForward.add((int)Math.round(50 * Math.sqrt(2)));
        unitsForward.add((int)Math.round(50 * Math.sqrt(2)));

        unitsForward.add(0);


        List<Double> headingAngles = calculateHeadings(xCoords, yCoords);

        for (int i = 0; i < headingAngles.size(); i++) {
            int currentX = xCoords.get(i);
            int currentY = yCoords.get(i);

            int nextX = xCoords.get(i+1);
            int nextY = yCoords.get(i+1);

            // System.out.println(String.format("Current Point: (%d, %d) -> Next Point: (%d, %d), Turn Angle: %f", currentX, currentY, nextX, nextY, headingAngles.get(i)));

            turtle.turn(headingAngles.get(i));
            turtle.forward(unitsForward.get(i));
        }
        
    }

    /**
     * Main method.
     * 
     * This is the method that runs when you run "java TurtleSoup".
     * 
     * @param args unused
     */
    public static void main(String args[]) {
        DrawableTurtle turtle = new DrawableTurtle();

        drawPersonalArt(turtle);

        // drawSquare(turtle, 40);
        // drawRegularPolygon(turtle, 5, 50);
        // double headtingToPointAngle = calculateHeadingToPoint(30, 0, 1, 0, 0);
        // System.out.println(String.format("Heading to Point Angle %f2", headtingToPointAngle));

        // draw the window
        turtle.draw();
    }

}

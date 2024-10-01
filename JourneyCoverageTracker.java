
package psprojectthree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Date;

/**
 *
 * @author Pradip Sapkota (3741235)
 */
public class PSProjectThree {
    // HashMap to store cell Towers and waypoints and Arraylist to store the joruneys
    private static HashMap <String, CellTower> cellTowers = new HashMap<>();
    private static ArrayList<String[]> journeys = new ArrayList<>();
    private static HashMap <String,Waypoint> waypoints = new HashMap<>();

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        
        //Prompt the user for celltower definitions files
        System.out.print("Enter the file name containing CellTower definitions: ");
        String cellTowerFile = input.nextLine();   // read the cell tower file 
        
        //Prompt the user for waypoint definitions file 
        System.out.print("Enter the file name containing Waypoint definitions: ");
        String waypointFile = input.nextLine();   //read the waypoint file 
        
        //Prompt thr user for file containing journeys definitons
        System.out.print("Enter the file name containing set of waypoint names for potential journeys: ");
        String journeyFile = input.nextLine();     //Read the journey file 

        System.out.println("");

        int skippedCellTowers = 0;    // Store the skipped cell towers(invalid celltowers)
        int cellTowersNumber = 0;     //Store the cell tower number 
        int validCelltowers=0;        // Store the valid cell towers 
        try
        {
            Scanner inputFile = new Scanner(new File(cellTowerFile));
            // Process each cell tower record from the cell tower file 
            while(inputFile.hasNext()){ 
               cellTowersNumber++;
               String cellTowerRecords = inputFile.nextLine();
               String [] tokens = cellTowerRecords.split(",");
               //Check if the record contains exactly four elements
               if(tokens.length==4){
                   try{
                       //Extract and parse cell tower attributes
                       String locationName = tokens[0];
                       double x = Double.parseDouble(tokens[1]);
                       double y = Double.parseDouble(tokens[2]);
                       double range = Double.parseDouble(tokens[3]);
                       
                       //Validate co-ordinates and throw an Exception if there are any
                       if(x<0 || y<0){
                           throw new IllegalArgumentException("Celltower record " + cellTowersNumber + " has invalid coordinate(s)." );  
                        } else if(range<=0){    // Validate the radius or the range and throw an Exception if there are any
                            throw new IllegalArgumentException("Celltower record " + cellTowersNumber + " has an invalid radius." );
                        }else{
                            validCelltowers++;    //Increment valid celltowers by 1
                            cellTowers.put(locationName, new CellTower(locationName,x,y,range));
                        }
                    } catch(NumberFormatException e) 
                    {
                        skippedCellTowers++;    //Increment skipped celltowers by 1
                        System.out.printf("  Skipping celltower record %d: %s%n",cellTowersNumber,cellTowerRecords);   
                        System.out.printf("    Number Format Exception in celltower record %d.%n",cellTowersNumber);
                        System.out.printf("    System message: %s\n",e.getMessage());
                    } catch (IllegalArgumentException e){
                        skippedCellTowers++;
                        System.out.printf("  Skipping celltower record %d: %s%n",cellTowersNumber,cellTowerRecords); 
                        System.out.printf("    %s%n", e.getMessage());
                    }
                   
                } else{
                   skippedCellTowers++;
                   System.out.printf("  Skipping celltower record %d: %s%n",cellTowersNumber,cellTowerRecords);
                   System.out.println("  Invalid format in the file");
               }
     
            }

        }
        catch(FileNotFoundException e)
        {
            System.out.println("Celltower file not found " + e.getMessage());
        }
        
        // Display the information for valid cell towers and skipped ones
        System.out.println("Information for " + validCelltowers + " cell towers defined. " + skippedCellTowers + " Records skipped.");
        System.out.println("");

        
        //Variables to track waypoints data for skipped, valid and total records
        int skippedWaypoints=0;
        int waypointNumber = 0;
        int waypointsCreated = 0;
        try
        {
            Scanner inputFile = new Scanner(new File(waypointFile));
            //Process each record in the waypoint file 
            while(inputFile.hasNextLine()){
                waypointNumber++;   // Increment by 1 for waypoint number 
                String waypointRecords = inputFile.nextLine().trim();
                if(waypointRecords.isEmpty()) continue;    
                String [] tokens = waypointRecords.split(",");
                if(tokens.length==3){
                  try{
                      String name = tokens[0];
                      double x = Double.parseDouble(tokens[1]);
                      double y = Double.parseDouble(tokens[2]);
                      //Validate the co-ordinates in the waypoint file 
                      if(x>=0 && y>=0){
                          waypointsCreated++;
                          waypoints.put(name, new Waypoint(name, x, y));
                      }else{
                          skippedWaypoints++;
                          System.out.printf("  Skipping celltower record %d: %s%n",waypointNumber,waypointRecords);
                          System.out.printf("    waypoint record %d has invalid coordinate(s).%n",waypointNumber); 
                      }
                    } catch(NumberFormatException e){
                        skippedWaypoints++;
                        System.out.printf("  Skipping waypoint record %d: %s%n",waypointNumber,waypointRecords);
                        System.out.printf("  Number Format Exception at record %d.%n",waypointNumber);
                        System.out.printf("  System message: %s\n",e.getMessage());  
                    } 
                } else{
                    skippedWaypoints++;
                    System.out.printf("  Skipping waypoint record %d: %s%n",waypointNumber,waypointRecords);
                    System.out.println("  Invalid format in the file  ");

                }
                
            }                    
        } catch (FileNotFoundException e)
        {
            System.out.println("Waypoint file not found " + e.getMessage());
        }
        
        // Display the information for valid waypoints and skipped ones
        System.out.println("Information for " + waypointsCreated + " waypoints defined. " + skippedWaypoints + " Records skipped.");
        System.out.println("");
        
        int journeyCount=0;     // Count each journey 
        int waypointCount = 0;    // Count eaxh waypoints
        
        try
        { 
            Scanner inputFile = new Scanner(new File(journeyFile));
            while(inputFile.hasNextLine()){
                journeyCount++;   // Increment the journey count 
                String journeyRecords = inputFile.nextLine();
                if(journeyRecords.trim().isEmpty()) continue;   // Skip empty lines
                String [] tokens = journeyRecords.split(",");   // Split the line into tokens
                ArrayList<String> validWaypoints = new ArrayList<>();   //ArrayList to store valid waypoints
   
                StringBuilder undefinedWaypoints = new StringBuilder();  // StringBuilder to store undefined waypoints

                boolean first = true;

                for (String name : tokens) {
                    name = name.trim();   // Remove leading and trailing whitespace
                    if (waypoints.containsKey(name)) {   // Check if the waypoint exists in the waypoints HashMap
                        validWaypoints.add(name);   // Add the valid waypoint to the list
                        waypointCount++;  // Increment the total waypoint count
                    } else {
                        if(!first){
                            undefinedWaypoints.append(", ");
                        }
                        undefinedWaypoints.append(name);
                        first = false;
                    }
                }
                if(!validWaypoints.isEmpty()){
                    journeys.add(validWaypoints.toArray(new String[0]));
                }
                // Print a message for any undefined waypoints and the valid waypoints in that journey 
                if (undefinedWaypoints.length() > 0) {
                    System.out.printf("  Skipping undefined waypoints %s in %s.%n",undefinedWaypoints,getOrdinal(journeyCount));
                    System.out.println("There are " + validWaypoints.size() + " waypoints in this journey.");  
                }
            }      
        }   
        catch(FileNotFoundException e)
        {
            System.out.println("Journey File not Found"+ e.getMessage());
        }
        
        
        //Generate the output file named 'Report.txt'
        try
            (PrintWriter outputFile = new PrintWriter("Report.txt")) {        //PrintWriter object to print the output in Report.txt textfile
            Date now = new Date();    // Date object for the real time solution
            outputFile.printf("%5sCellphone Tower Coverage Report%n","");  //done here
            outputFile.println("Prepared on: " + now.toString());
            outputFile.println();

            for (int i = 0; i < journeys.size(); i++) {
                String[] journey = journeys.get(i);

                //Print the number of waypoints in that journey
                outputFile.println("The " + getOrdinal(i + 1) + " consists of " + journey.length + " waypoints:");
                // Loop through each waypoint in the journey and print it
                for (String waypoint : journey) {
                    outputFile.print(waypoint + "  ");     //Print the waypoints
                }
                outputFile.println();   //For the blank line 
                outputFile.println();   //For the blank line

                outputFile.printf("%29sMid-point:%n","");  
                outputFile.printf("%1sFrom%5sTo%4sDistance%2sX-coord%2sY-coord%n","","","","",""); 
                outputFile.printf("------%2s------%2s--------%2s-------%2s-------%n","","","","");  
                
                // Calculate and print the distance between each pair of waypoints if there are at least two waypoints
                if(journey.length > 1){
                  for (int j = 0; j < journey.length - 1; j++) {
                    String from = journey[j];
                    String to = journey[j+1];
                    Waypoint fromPoint = waypoints.get(from);  //Get from point
                    Waypoint toPoint = waypoints.get(to);     //Get to point
                    double distance = calculateWayointDistance(fromPoint,toPoint);   //Call the calculateWayointDistance method to calculate distance
                    double midX = (fromPoint.getX() + toPoint.getX()) / 2;   //Calculate the mid points between two waypoints for x-coordinate
                    double midY = (fromPoint.getY() + toPoint.getY()) / 2;   //Calculate the mid points between two waypoints for y-coordinate
                    outputFile.printf("%2s%4s%4s%4s%5s%4.2f%4s%5.2f%4s%5.2f\n","", fromPoint.getName(),"", toPoint.getName(),"", distance,"", midX,"", midY); 
                  }  
                }
                outputFile.println();  //Leave the blank line 

                // Print the location, tower, and proximity for each waypoint
                outputFile.printf("Location%3sTower%3sProximity%n","",""); //removed - here
                outputFile.printf("--------%2s-------%2s---------%n","","");   
                
                for (int k=0; k<journey.length; k++){
                    Waypoint wp1 = waypoints.get(journey[k]);    //Get the first waypoint object
                    outputFile.printf("%1s%4s%6s%15s%n", "",wp1.getName(),"", checkCoverage(wp1.getX(),wp1.getY())); 
                    
                    // If there's a next waypoint, calculate and print the mid-point coverage details
                    if(k<journey.length-1){
                        Waypoint wp2 = waypoints.get(journey[k+1]);   //Get the second waypoint object
                        double midX = (wp1.getX()+ wp2.getX()) / 2;  // Calculate the mid-point X coordinate
                        double midY = (wp1.getY()+ wp2.getY()) / 2;  // Calculate the mid-point y coordinate
                        outputFile.printf("%1sMid-Pt%4s%15s%n", "","",checkCoverage(midX,midY));  
                    }
                }
                outputFile.println();   //Leave the blank line 
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to create report file: " + e.getMessage());
        }
    }
    
     /**
     * Returns the ordinal representation of a number.
     * @param number the number to be converted to an ordinal
     * @return the ordinal string
     */
    private static String getOrdinal(int number){
        switch(number){
            case 1:
                return "First Journey";
            case 2:
                return "Second journey";
            case 3:
                return "Third journey";
            case 4:
                return "Fourth journey";
            case 5:
                return "Fifth journey";
            case 6:
                return "Sixth journey";
            case 7:
                return "Seventh journey";
            case 8:
                return "Eighth journey";
            case 9:
                return "Ninth journey";
            case 10:
                return "Tenth journey";  
            default:
                return number + "th journey";
        }
    }
    /**
     * Calculates the distance between two points.
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the distance between the two points
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
     /**
     * Determines the nearest cell tower and its distance from a given location.
     * @param x the x-coordinate of the location
     * @param y the y-coordinate of the location
     * @return a formatted string with the nearest tower's name and distance, or "No Coverage" if out of range
     */
    private static String checkCoverage(double x, double y){
        CellTower nearestTower = null;
        double minDistance = Double.MAX_VALUE;
        
        for(CellTower tower: cellTowers.values()){
            double distance = calculateDistance(x, y, tower.getX(), tower.getY());
            if(distance < minDistance){
                minDistance = distance;
                nearestTower = tower;
            }
        }
        if (nearestTower != null && minDistance <= nearestTower.getRange()) {
                return String.format("%5s%7s%3.1f",nearestTower.getName(),"",minDistance); 
            
        } 
            return String.format("%1s", "No Coverage.");
    }
     /**
     * Calculates the  distance between two waypoints.
     * @param wp1 the first waypoint
     * @param wp2 the second waypoint
     * @return the distance between the two waypoints
     */
    private static double calculateWayointDistance(Waypoint wp1,Waypoint wp2) {
        return Math.sqrt(Math.pow(wp2.getX()-wp1.getX(),2)+Math.pow(wp2.getY()- wp1.getY(), 2));
    }        
}

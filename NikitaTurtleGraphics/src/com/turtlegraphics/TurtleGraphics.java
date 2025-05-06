package com.turtlegraphics;

import uk.ac.leedsbeckett.oop.LBUGraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;

public class TurtleGraphics extends LBUGraphics {
    
    private JFrame mainFrame;
    private JTextArea commandHistoryArea;
    private boolean imageSaved = true;
    private boolean isPenDown = true;

    // Constructor to initialize the turtle graphics application
    public TurtleGraphics() {
        // Create the main application frame
        mainFrame = new JFrame("Turtle Graphics");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set up the menu bar
        createMenuBar();
        
        // Set up the main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Add turtle graphics canvas
        mainPanel.add(this, BorderLayout.CENTER);
        
        // Initialize the text area for displaying command history
        commandHistoryArea = new JTextArea(5, 30);
        commandHistoryArea.setEditable(false);
        
        // Add scrolling capability to the command history area
        JScrollPane scrollPane = new JScrollPane(commandHistoryArea);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Add main panel to frame and display it
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
        
        // Initialize turtle's default state
        reset();
        setPenColour(Color.red);
        setStroke(3);
        setPenState(true);
        
        // Display welcome message
        displayMessage("Welcome to Turtle Graphics! Type 'help' for commands.");
        appendToCommandHistory("Application started. Type 'help' for commands.");
        
        // Create status bar
        createStatusBar();
    }
 
	// Append text to the command history area
    // Adds a line of text to the JTextArea showing previously executed commands
    private void appendToCommandHistory(String command) {
        commandHistoryArea.append(command + "\n");
    }

    // Create and configure the menu bar
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        
        // Create "Save" with options to save image and text
        JMenu saveMenu = new JMenu("Save");
        JMenuItem saveImageItem = new JMenuItem("Save as Image");
        saveImageItem.setToolTipText("Save the current canvas as a PNG image");
        saveImageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageFile();
            }
        });
        
        // Menu item to save command history as a text file
        JMenuItem saveTextItem = new JMenuItem("Save command as Text");
        saveTextItem.setToolTipText("Save the command history to a text file");
        saveTextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTextFile();       // Call method to save text
            }
        });
        
        // Add both save options to the "Save" menu
        saveMenu.add(saveImageItem);
        saveMenu.add(saveTextItem);
        
        // Create "Load" submenu with options to load image and text
        JMenu loadMenu = new JMenu("Load");
        
        // Menu item to load an image onto the canvas
        JMenuItem loadImageItem = new JMenuItem("Load Image");
        loadImageItem.setToolTipText("Load an image onto the canvas");
        loadImageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImageFile();     // Call method to load image
            }
        });
        
        // Menu item to load commands from a text file
        JMenuItem loadTextItem = new JMenuItem("Load commands from Text");
        loadTextItem.setToolTipText("Load command history from a text file");
        loadTextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTextFile();    // Call method to load text commands
            }
        });
        
        // Add both load options to the "Load" menu
        loadMenu.add(loadImageItem);
        loadMenu.add(loadTextItem);
        
        // Create "Exit" menu item to close the application
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setToolTipText("Exit the application");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If there are unsaved changes, confirm before exiting
                if (!imageSaved) {
                    int response = JOptionPane.showConfirmDialog(mainFrame,
                            "You have unsaved changes. Exit anyway?",
                            "Confirm Exit", JOptionPane.YES_NO_OPTION);
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                System.exit(0);
            }
        });
        
        // Add menus to the file menu
        fileMenu.add(saveMenu);
        fileMenu.add(loadMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
     // Create the "Help" menu with a help option
        JMenu helpMenu = new JMenu("Help");
     // Menu item that displays available commands
        JMenuItem helpItem = new JMenuItem("Command Help");
        helpItem.setToolTipText("View available commands");
     // An action listener to show help when clicked
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
        helpMenu.add(helpItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        mainFrame.setJMenuBar(menuBar);
    }

    // Save the current canvas as an image
    private void saveImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        fileChooser.setSelectedFile(new File("drawing.png"));
        
        int userSelection = fileChooser.showSaveDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try {
                BufferedImage image = getBufferedImage();
                ImageIO.write(image, "png", fileToSave);
                displayMessage("Drawing saved as" + fileToSave);
                appendToCommandHistory("Saved image to:" + fileToSave.getName());
                imageSaved = true;
            } catch (IOException e) {
            	displayMessage("Error saving image:" + e.getMessage());
                appendToCommandHistory("Saved image to:" + e.getMessage());
            }
        }
    }

    // Save the command history to a text file
    private void saveTextFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Command History");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        if (chooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try {
                java.nio.file.Files.write(file.toPath(), commandHistoryArea.getText().getBytes());
                JOptionPane.showMessageDialog(mainFrame, "Command history saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Load an image onto the canvas
    private void loadImageFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Image");
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));

        if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);
                Graphics g = getGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                JOptionPane.showMessageDialog(mainFrame, "Image loaded successfully.");
                appendToCommandHistory("Loaded image: " + file.getName());
                imageSaved = false;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error loading image: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Load command history from a text file and execute each command
    private void loadTextFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Commands");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        
        // If user approves file selection
        if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                // Process each line as a command
                String[] commands = content.split("\n");
                for (String cmd : commands) {
                    processCommand(cmd.trim());
                }
                JOptionPane.showMessageDialog(mainFrame, "Commands loaded successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error loading file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Show help dialog for listing available commands
    private void showHelp() {
        // Display a message dialog with a list of supported text commands
        JOptionPane.showMessageDialog(mainFrame, "Available commands:\n" +
            "move <distance> - Move the turtle forward\n" +
            "turn <angle> - Turn the turtle right by the specified angle\n" +
            "pen up - Lift the pen\n" +
            "pen down - Put the pen down\n" +
            "help - Show this help message", "Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Create a status bar at the bottom of the window
    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Pen: DOWN | Color: Red | Position: (0,0)");
        statusPanel.add(statusLabel);
        mainFrame.add(statusPanel, BorderLayout.SOUTH);
    }
    
    // Process a single text command entered by the user
    @Override
    public void processCommand(String command) {
        if (command == null || command.isEmpty()) return;
        appendToCommandHistory(command);
        imageSaved = false;

        String cmd = command.toLowerCase().trim();
        System.out.println("Processed command: " + cmd);
        String[] parts = cmd.split("\\s+"); // Split the command into parts based on spaces

        try {
            switch (parts[0]) {
                case "name":
                    about();
                    appendToCommandHistory("Name Drawn");
                    imageSaved = false;
                    break;
                    
                case "about":
                	superAbout();
                	break;
                	
                case "penstate":
                    if (parts.length == 1) {
                        displayMessage("Pen is currently " + (isPenDown ? "down." : "up."));
                        appendToCommandHistory("Checked pen state: " + (isPenDown ? "down" : "up"));
                    } else {
                        displayMessage("Syntax: penstate");
                        appendToCommandHistory("Error: Syntax: penstate");
                    }
                    break;	

                case "penup":
                    if (parts.length == 1) {
                        setPenState(false);
                        isPenDown = false;
                        displayMessage("Pen lifted.");
                        appendToCommandHistory("Pen lifted.");
                        imageSaved = false;
                    } else {
                        displayMessage("Syntax: penup");
                        appendToCommandHistory("Error: Syntax: penup");
                    }
                    break;
                   
                case "pendown":
                    if (parts.length == 1) {
                        setPenState(true);
                        isPenDown = true;
                        displayMessage("Pen lowered.");
                        appendToCommandHistory("Pen lowered.");
                        imageSaved = false;
                    } else {
                        displayMessage("Syntax: pendown");
                        appendToCommandHistory("Error: Syntax: pendown");
                    }
                    break;

                case "left":
                    if(parts.length == 2) {
                        left(Integer.parseInt(parts[1]));  // Fixed: call left() instead of forward()
                        appendToCommandHistory("Turned left by " + parts[1] + " degrees");  // Added space
                    } else {
                        left(90);
                        appendToCommandHistory("Turned left by 90 degrees");
                    }
                    break;

                case "right":
                    if(parts.length == 2) {
                        right(Integer.parseInt(parts[1]));  // Fixed: call right() instead of forward()
                        appendToCommandHistory("Turned right by " + parts[1] + " degrees");  // Added space
                    } else {
                        right(90);
                        appendToCommandHistory("Turned right by 90 degrees");
                    }
                    break;

                case "move":
                    int distance = 100; // default distance
                    if(parts.length == 2) {
                        try {
                            distance = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Distance must be a number.");
                            appendToCommandHistory("Error: Distance must be a number.");
                            break;
                        }
                    } else if(parts.length > 2) {
                        displayMessage("Syntax: move OR move <distance>");
                        appendToCommandHistory("Error: Syntax: move OR move <distance>");
                        break;
                    }

                    if(isPenDown) {
                        forward(distance);
                    } else {
                        penUp();
                        forward(distance);
                        penDown();
                    }
                    appendToCommandHistory("Moved forward by " + distance + " pixels");
                    imageSaved = false;
                    break;
                    
                case "reverse":
                    int reverseDistance = 100; // default reverse distance
                    if(parts.length == 2) {
                        try {
                            reverseDistance = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            displayMessage("Error: Distance must be a number.");
                            appendToCommandHistory("Error: Distance must be a number.");
                            break;
                        }
                    } else if(parts.length > 2) {
                        displayMessage("Syntax: reverse OR reverse <distance>");
                        appendToCommandHistory("Error: Syntax: reverse OR reverse <distance>");
                        break;
                    }

                    if(isPenDown) {
                        forward(-reverseDistance);
                    } else {
                        penUp();
                        forward(-reverseDistance);
                        penDown();
                    }
                    appendToCommandHistory("Moved backward by " + reverseDistance + " pixels");
                    imageSaved = false;
                    break;

                case "clear":
                    if (parts.length == 1) {
                        // Only show warning if there are unsaved changes
                        if (!imageSaved) {
                            int response = JOptionPane.showConfirmDialog(
                                mainFrame,
                                "You have unsaved changes. Clear the canvas anyway?",
                                "Confirm Clear Canvas",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                            );
                            
                            if (response != JOptionPane.YES_OPTION) {
                                appendToCommandHistory("Clear operation cancelled by user");
                                return; // User chose not to clear
                            }
                        }
                        
                        // Clear the canvas and update state
                        clear();
                        displayMessage("Canvas cleared.");
                        appendToCommandHistory("Canvas cleared.");
                        imageSaved = false; // Blank canvas needs saving
                        repaint();
                    } else {
                        displayMessage("Syntax error: Use 'clear' (no parameters)");
                        appendToCommandHistory("Error: Invalid clear command syntax");
                    }
                    break;

                case "reset":
                    if (parts.length == 1) {
                        // Only show warning if there are unsaved changes
                        if (!imageSaved) {
                            int response = JOptionPane.showConfirmDialog(
                                mainFrame,
                                "You have unsaved changes. Reset turtle and canvas anyway?",
                                "Confirm Reset",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                            );
                            
                            if (response != JOptionPane.YES_OPTION) {
                                appendToCommandHistory("Reset operation cancelled by user");
                                return; // User chose not to reset
                            }
                        }
                        
                        // Reset everything to default state
                        reset();
                        setPenColour(Color.BLACK);
                        setStroke(1);
                        setPenState(true);
                        isPenDown = true;
                        
                        displayMessage("Canvas and turtle reset to default state.");
                        appendToCommandHistory("Canvas and turtle reset.");
                        imageSaved = false; // Blank canvas needs saving
                        repaint();
                    } else {
                        displayMessage("Syntax error: Use 'reset' (no parameters)");
                        appendToCommandHistory("Error: Invalid reset command syntax");
                    }
                    break;
                    
                case "triangle":
                    if (parts.length == 2) {
                        try {
                            int size = Integer.parseInt(parts[1]);
                            if (size <= 0) {
                                displayMessage("Triangle size must be positive.");
                                appendToCommandHistory("Error: Triangle size must be positive.");
                                break;
                            }
                            drawEquilateralTriangle(size);
                            appendToCommandHistory("Drew equilateral triangle with size " + size);
                            imageSaved = false;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid triangle size. Please enter a number.");
                            appendToCommandHistory("Error: Invalid triangle size.");
                        }
                    } else if (parts.length == 4) {
                        try {
                            int a = Integer.parseInt(parts[1]);
                            int b = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);

                            if (a <= 0 || b <= 0 || c <= 0) {
                                displayMessage("All triangle sides must be positive.");
                                appendToCommandHistory("Error: Triangle sides must be positive.");
                                break;
                            }

                            if (isValidTriangle(a, b, c)) {
                                drawTriangle(a, b, c);
                                appendToCommandHistory("Drew triangle with sides " + a + ", " + b + ", " + c);
                                imageSaved = false;
                            } else {
                                displayMessage("Invalid triangle sides. The sum of any two sides must be greater than the third.");
                                appendToCommandHistory("Error: Invalid triangle dimensions.");
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid triangle sides. Please enter three numbers.");
                            appendToCommandHistory("Error: Invalid triangle side inputs.");
                        }
                    } else {
                        displayMessage("Syntax: triangle <size> or triangle <side1> <side2> <side3>");
                        appendToCommandHistory("Error: Syntax: triangle <size> or triangle <side1> <side2> <side3>");
                    }
                    break;
                  
                case "square":
                	if (parts.length == 2) {
                        drawSquare(Integer.parseInt(parts[1]));
                        appendToCommandHistory("Drew square with size " + parts[1]);
                        imageSaved = false;
                	} else {
                		displayMessage("Syntax: square <size>");
                        appendToCommandHistory("Error: Syntax: square <size>");
                	}
                	break;
                	
                case "star":
                    if (parts.length == 2) {
                        try {
                            int size = Integer.parseInt(parts[1]);
                            drawStar(size);
                            appendToCommandHistory("Drew star with size " + size);
                            imageSaved = false;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid size. Please enter a number.");
                            appendToCommandHistory("Error: Invalid star size");
                        }
                    } else {
                        displayMessage("Syntax: star <size>");
                        appendToCommandHistory("Error: Syntax: star <size>");
                    }
                    break;	
                    
                case "gradient":
                    if (parts.length == 4) {
                        try {
                            Color startColor = parseColor(parts[1]);
                            Color endColor = parseColor(parts[2]);
                            int steps = Integer.parseInt(parts[3]);
                            drawGradientLine(startColor, endColor, steps);
                            appendToCommandHistory("Drew gradient line from " + parts[1] + " to " + parts[2]);
                            imageSaved = false;
                        } catch (Exception e) {
                            displayMessage("Error: " + e.getMessage());
                            appendToCommandHistory("Error in gradient: " + e.getMessage());
                        }
                    } else {
                        displayMessage("Syntax: gradient <startColor> <endColor> <steps>");
                        appendToCommandHistory("Error: Syntax: gradient <startColor> <endColor> <steps>");
                    }
                    break;
         
                case "save":
                	if (parts.length == 2) {
                        saveDrawing(parts[1]);
                        appendToCommandHistory("Saved drawing as " + parts[1]);
                	} else {
                		displayMessage("Syntax: save <filename>");
                        appendToCommandHistory("Error: Syntax: save <filename>");
                	}
                	break;
                	
                case "load":
                    if (parts.length == 2) {
                        if (!imageSaved) {
                            int response = JOptionPane.showConfirmDialog(mainFrame,
                                "You have unsaved changes. Load new image anyway?",
                                "Confirm Load", JOptionPane.YES_NO_OPTION);
                            if (response != JOptionPane.YES_OPTION) {
                                return; // User chose not to load
                            }
                        }
                        loadDrawing(parts[1]);
                        appendToCommandHistory("Loaded drawing as " + parts[1]);
                        imageSaved = false; // Loading a new image needs saving
                    } else {
                        displayMessage("Syntax: load <filename>");
                        appendToCommandHistory("Error: Syntax: load <filename>");
                    }
                    break;
                    
                case "pencolour":
                    if (parts.length == 2) {
                        String colourStr = parts[1].toLowerCase();
                        try {
                            Color newColour;
                            
                            // Check for RGB format (r,g,b)
                            if (colourStr.matches("\\d+,\\d+,\\d+")) {
                                String[] rgb = colourStr.split(",");
                                int r = Integer.parseInt(rgb[0].trim());
                                int g = Integer.parseInt(rgb[1].trim());
                                int b = Integer.parseInt(rgb[2].trim());
                                newColour = new Color(
                                    Math.max(0, Math.min(255, r)),
                                    Math.max(0, Math.min(255, g)),
                                    Math.max(0, Math.min(255, b))
                                );
                            }
                            // Check for hex format (#RRGGBB)
                            else if (colourStr.startsWith("#") && colourStr.length() == 7) {
                            	newColour = Color.decode(colourStr);
                            }
                            // Named colors
                            else {
                                switch (colourStr) {
                                    case "black": newColour = Color.BLACK; break;
                                    case "white": newColour = Color.WHITE; break;
                                    case "red": newColour = Color.RED; break;
                                    case "green": newColour = Color.GREEN; break;
                                    case "blue": newColour = Color.BLUE; break;
                                    case "yellow": newColour = Color.YELLOW; break;
                                    case "cyan": newColour = Color.CYAN; break;
                                    case "magenta": newColour = Color.MAGENTA; break;
                                    case "orange": newColour = Color.ORANGE; break;
                                    case "pink": newColour = Color.PINK; break;
                                    case "gray": newColour = Color.GRAY; break;
                                    default: throw new IllegalArgumentException("Unknown color name");
                                }
                            }
                            
                            // Debug output
                            System.out.println("Setting color to: " + newColour);
                            
                            // Set the color
                            setPenColour(newColour);
                            appendToCommandHistory("Pen color set to " + colourStr);
                            imageSaved = false;
                            
                            // Immediate test - draw a small line to see color change
                            if (isPenDown) {
                                forward(10);
                            }
                            
                        } catch (Exception e) {
                            displayMessage("Invalid color. Use: name, #RRGGBB, or r,g,b (0-255)");
                            appendToCommandHistory("Error: " + e.getMessage());
                        }
                    } else {
                        displayMessage("Syntax: pencolor <color>");
                        appendToCommandHistory("Error: Syntax: pencolor <color>");
                    }
                    break;
                    
                case "penwidth":
                    if (parts.length == 2) {
                        try {
                            int width = Integer.parseInt(parts[1]);
                            setStroke(width);
                            displayMessage("Pen width set to " + width);
                            appendToCommandHistory("Set pen width to " + width);
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid width - must be an integer.");
                            appendToCommandHistory("Error: Invalid width - must be an integer.");
                        }
                    } else {
                        displayMessage("Syntax: penwidth <size>");
                        appendToCommandHistory("Error: Syntax: penwidth <size>");
                    }
                    break;
                    
                case "setspeed":
                    if (parts.length == 2) {
                        try {
                            setTurtleSpeed(Integer.parseInt(parts[1]));
                            displayMessage("Turtle speed set to " + parts[1]);
                            appendToCommandHistory("Set turtle speed to " + parts[1]);
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid speed. Speed must be an integer.");
                            appendToCommandHistory("Error: Invalid speed, must be an integer.");
                        }
                    } else {
                        displayMessage("Syntax: setspeed <1-10>");
                        appendToCommandHistory("Error: Syntax: setspeed <1-10>");
                    }
                    break;

                case "help":
                	showHelp();
                	appendToCommandHistory("Displayed help");
                    break;
                    
                default:
                    displayMessage("Invalid command: " + cmd);
                    System.out.println("Error: Invalid command : " + cmd);
            }
        } catch (NumberFormatException e) {
            displayMessage("Please enter a valid number.");
        	appendToCommandHistory("Error: Please enter a valid number");
        } catch (ArrayIndexOutOfBoundsException e) {
        	displayMessage("Missing parameter for command");
        	appendToCommandHistory("Error: Missing parameter for command");
        } catch (Exception e) {
        	displayMessage("Error:" + e.getMessage());
        	appendToCommandHistory("Error:" + e.getMessage());
        }
    }
   
    // Draw square
    private void drawSquare(int size) {
        if (size <= 0) {
            displayMessage("Size must be positive");
            return;
        }
        setPenState(true);
        for(int i=0; i<4; i++) {
            forward(size);
            left(90);
        }
    }    
    
    private void saveDrawing(String filename) {
        try {
            BufferedImage image = getBufferedImage();            
            ImageIO.write(image, "PNG", new File(filename + ".png"));
            appendToCommandHistory("Drawing saved as " + filename + ".png");
            imageSaved = true;
        } catch (IOException e) {
            displayMessage("Error saving file: " + e.getMessage());
            appendToCommandHistory("Error: Failed to save " + e.getMessage());
        }
    }
    
    // Adding methods to TurtleGraphicsApp class:
    // Draw an equilateral triangle of the given side length
    private void equilateralTriangle(int size) {
    	setPenState(true);
    	for (int i = 0; i<3; i++) {
    		forward(size);
    		left(120);
    	}
    }
    
    // Triangle validation method
    private boolean isValidTriangle(int a, int b, int c) {
        return (a + b > c) && (a + c > b) && (b + c > a);
    }
    
 // Draw a 5-pointed star
    private void drawStar(int size) {
        if (size <= 0) {
            displayMessage("Size must be positive");
            return;
        }
        
        setPenState(true);
        for (int i = 0; i < 5; i++) {
            forward(size);
            right(144);
        }
    }
    
 // Gradient drawing method
    private void drawGradientLine(Color startColor, Color endColor, int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }
        
        int startR = startColor.getRed();
        int startG = startColor.getGreen();
        int startB = startColor.getBlue();
        int deltaR = endColor.getRed() - startR;
        int deltaG = endColor.getGreen() - startG;
        int deltaB = endColor.getBlue() - startB;
        
        setPenState(true);
        for (int i = 0; i <= steps; i++) {
            float ratio = (float) i / (float) steps;
            int r = startR + (int) (deltaR * ratio);
            int g = startG + (int) (deltaG * ratio);
            int b = startB + (int) (deltaB * ratio);
            setPenColour(new Color(r, g, b));
            forward(1);
        }
    }
    
 // Load drawing from file (simplified version)
 private void loadDrawing(String filename) {
     try {
         BufferedImage image = ImageIO.read(new File(filename));
         getGraphics().drawImage(image, 0, 0, null);
         appendToCommandHistory("Loaded drawing from " + filename);
     } catch (IOException e) {
         displayMessage("Error loading file: " + e.getMessage());
         appendToCommandHistory("Error loading file: " + e.getMessage());
     }
 }

 private void penDown() {
	// TODO Auto-generated method stub	
}

private void penUp() {
	// TODO Auto-generated method stub	
}

// Parse color from string input
 private Color parseColor(String colorStr) throws IllegalArgumentException {
     colorStr = colorStr.toLowerCase();
     
     // Check for named colors
     switch (colorStr) {
         case "black": return Color.BLACK;
         case "white": return Color.WHITE;
         case "red": return Color.RED;
         case "green": return Color.GREEN;
         case "blue": return Color.BLUE;
         case "yellow": return Color.YELLOW;
         case "cyan": return Color.CYAN;
         case "magenta": return Color.MAGENTA;
         case "orange": return Color.ORANGE;
         case "pink": return Color.PINK;
     }
     
     // Check for hex color
     if (colorStr.startsWith("#")) {
         try {
             return Color.decode(colorStr);
         } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid hex color format");
         }
     }
     
     // Check for RGB format
     if (colorStr.contains(",")) {
         String[] rgb = colorStr.split(",");
         if (rgb.length == 3) {
             try {
                 int r = Integer.parseInt(rgb[0].trim());
                 int g = Integer.parseInt(rgb[1].trim());
                 int b = Integer.parseInt(rgb[2].trim());
                 return new Color(r, g, b);
             } catch (NumberFormatException e) {
                 throw new IllegalArgumentException("Invalid RGB values");
             }
         }
     }
     
     throw new IllegalArgumentException("Unrecognized color format");
 }
 
 private void showError(String message) {
	    displayMessage("Error: " + message);
	    appendToCommandHistory("Error: " + message);
	}
  
 // Remove the orphaned 'set' keyword - it appears around line 447 in your original code
 // Just delete this line that only contains: set  
    
    public void superAbout()
    {
    	super.about();   // Invoke the about() method from LBUGraphics (the superclass)
    }
    
    // Drawing NIKITA
    public void about() {
    	setTurtleSpeed(1);
    	reset();
    	setPenState(false);
    	setPenColour(Color.MAGENTA);
    	setStroke(4);
    	
      // letter N
  	  right();
  	  forward(250);
  	  right();

  	  setPenColour(Color.ORANGE);
  	  setPenState(true);
  	  forward(100);
  	  right(145);
  	  forward(130);
  	  left(140);
  	  forward(110);
        
  	  // letter I 
  	  setPenColour(Color.GREEN);
  	  setPenState(false);
  	  right();
  	  left(4);
  	  forward(40);
  	  setPenState(true);
  	  forward(40);
  	  setPenState(false);
  	  forward(-20);
  	  right();
  	  setPenState(true);
  	  forward(100);
  	  right();
  	  setPenState(false);
  	  forward(20);
  	  setPenState(true);
  	  forward(-40);
  	  
  	  // letter K
  	  setPenState(false);
  	  right(180);
  	  forward(40);
  	  setPenColour(Color.RED);
  	  left();
  	  setPenState(true);
  	  forward(100);
  	  forward(-50);
  	  right(45);
  	  forward(60);
  	  forward(-60);
  	  right(90);
  	  forward(70);
  	  
  	  // letter I
  	  setPenState(false);
  	  left(45);
  	  forward(40);
  	  setPenColour(Color.WHITE);
  	  left();
  	  forward(100);
  	  left();
  	  setPenState(true);
  	  forward(20);
  	  forward(-40);
  	  forward(20);
      left();
  	  forward(100);
  	  right();
  	  setPenState(false);
  	  forward(20);
  	  setPenState(true);
  	  forward(-40);
      right(180);
        
  	  // letter T
  	  setPenColour(Color.YELLOW);
  	  setPenState(false);
  	  forward(60);
  	  left();
  	  forward(100);
      right();
      forward(-20);
  	  setPenState(true); 
  	  forward(40);
  	  forward(-20);
      right(); 
  	  forward(100);
  	  setPenState(false);
  	  
  	  // letter A
  	  setPenColour(Color.PINK);
  	  left();
  	  forward(60);
  	  left();
  	  setPenState(true); 
  	  forward(100);
  	  right();
  	  forward(40);
  	  right();
  	  forward(100);
  	  setPenState(false);
  	  right();
  	  forward(40);
  	  right();
  	  forward(50);
  	  right();
  	  setPenState(true); 
  	  forward(40);
  	  setPenState(false);
    }

    private void displayHelp() {
        String helpText = "Available commands:\n" +
                "about - Show about information\n" +
                "penup - Lift the pen\n" +
                "pendown - Lower the pen\n" +
                "penstate - Show pen state\n" +
                "left <degrees> - Turn left\n" +
                "right <degrees> - Turn right\n" +
                "move <distance> - Move forward\n" +
                "reverse <distance> - Move backward\n" +
                "clear - Clear the canvas\n" +
                "reset - Reset turtle and canvas\n" +
                "square <size> - Draw a square\n" +
                "triangle <size> - Draw equilateral triangle\n" +
                "triangle <a,b,c> - Draw triangle with sides a,b,c\n" +
                "pencolor <r,g,b> - Set pen color (0-255)\n" +
                "penwidth <size> - Set pen width\n" +
                "help - Show this help";
        displayMessage(helpText);
    }

    // --- Triangle Drawing Methods ---
    private void drawEquilateralTriangle(int size) {
        setPenState(true);
        for (int i = 0; i < 3; i++) {
            forward(size);
            right(120);
        }
    }

    private void drawTriangle(int a, int b, int c) {
    	setPenState(true);
        // Very basic triangle drawing assuming angles ~60° just to show different sizes
        forward(a);
        right(120);
        forward(b);
        right(120);
        forward(c);
        right(120);
    }

    // --- Helper Methods ---
    private void for_positive(int a) {
        if (a <= 0) {
            throw new IllegalArgumentException("Value must be a positive integer.");
        }
    }

    private void checkParameters(String[] parts, int count) {
        if (parts.length < count) {
            throw new IllegalArgumentException("Missing parameters. Expected " + count + " but got " + parts.length);
        }
    }

    private int getPositiveInt(String input) {
        int val = Integer.parseInt(input);
        if (val < 0) {
            throw new IllegalArgumentException("Value must be positive.");
        }
        return val;
    }    

    private void backward(int distance) {
        forward(-distance); 
    }
}
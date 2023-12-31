
import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;

/*
    Algorithm to find how to colour a map using 3 colours:

    1. Assign a random colour to the state with the most borders
    2. Pick a one of the countries that neighbor it with the least amount of neighbors
        a. Assign it one of the 2 remaining colours
        b. Pick a country that borders it with 2 coloured neighbors and is uncoloured
        loop back to a until there are no bordering countries that are uncoloured
    3. loop through all countries and check if they are coloured
            if not coloured, pick one of the remaining colours left by its neighbors
                if no neighbors, assign first colour option in list/array of colours
*/

public class Graph_Colouring_TriColoured {
    public static class cNode {
        public Color defaultColour = Color.DARK_GRAY;
        public Color colour;
        public String id;
        public final int x, y, size_x, size_y;
        public cNode[] Bordering;
        public cNode[] UncolouredBordering;

        public cNode(String n_id, int x_coordinate, int y_coordinate) {
            id = n_id;
            x = x_coordinate;
            y = y_coordinate;
            size_x = 60;
            size_y = 60;
            colour =  defaultColour;
        }
        public void setBordering(cNode[] arr) {
            Bordering = arr;
            UncolouredBordering = arr;
        }
        public void BorderColoured(cNode coloured_state) {
            int newlength = UncolouredBordering.length-1; // removing one element, so one shorter
            cNode[] tmp = new cNode[newlength];
            int index = 0;
            for (Graph_Colouring_TriColoured.cNode cNode : UncolouredBordering) {
                if (cNode != coloured_state && index < tmp.length) {
                    tmp[index] = cNode;
                    index++;
                }
            }
            UncolouredBordering = tmp;
        }

        public void setColour(Color newColour) {
            colour = newColour;
            for (cNode state : UncolouredBordering) {  // Only needed for uncoloured neighbors
                state.BorderColoured(this);
            }
        }

        public cNode[] findBorders(cNode[][] grid, int row, int column) {
            int numRows = grid.length;
            int numCols = grid[0].length;

            int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}, {1,1}, {-1,-1}}; // Up, Right, Down, Left

            int validNeighborCount = 0;

            for (int[] direction : directions) { // finds num of valid neighbors
                int newX = column + direction[0];
                int newY = row + direction[1];

                if (newX >= 0 && newX < numCols && newY >= 0 && newY < numRows) {
                    validNeighborCount++;
                }
            }

            cNode[] validNeighbors = new cNode[validNeighborCount];

            int index = 0;
            for (int[] direction : directions) {
                int newX = column + direction[0];
                int newY = row + direction[1];

                if (newX >= 0 && newX < numCols && newY >= 0 && newY < numRows) {
                    validNeighbors[index] = grid[newY][newX];
                    index++;
                }
            }

            return validNeighbors;
        }
    }
    public static Color find_colour(cNode state, Color[] colors) {
        Color[] available = colors;

        if (state.Bordering.length > 0) {
            System.out.println("state:"+state.id+"| Neighbors: "+state.Bordering.length);
            for (cNode Neighbor : state.Bordering) {
                System.out.println("Neighbor: "+Neighbor.id);
            }

            for (cNode Neighbor : state.Bordering) {
                if (Neighbor.colour != Neighbor.defaultColour) { // update list of available colours if one is not available
                    Color[] temp = new Color[available.length];

                    int index = 0;
                    for (int i = 0; i < temp.length; i++) {
                        if (available[i] != Neighbor.colour) {
                            temp[index] = available[i];
                            index++;
                        }
                    }
                    available = temp;
                }
            }
            if (available.length != 0) {
                return available[0];
            } else {
                return state.defaultColour;
            }
        } else {
            return available[0];
        }
    }

    public static void main(String[] args) {
        final Color[] map_colours = {Color.RED, Color.GREEN, Color.BLUE};
        // Create Nodes
        cNode A1 = new cNode("A1", 10, 10);
        cNode B1 = new cNode("B1", 110, 10);
        cNode C1 = new cNode("C1", 210, 10);
        cNode D1 = new cNode("D1", 310, 10);

        cNode A2 = new cNode("A2", 10, 110);
        cNode B2 = new cNode("B2", 110, 110);
        cNode C2 = new cNode("C2", 210, 110);
        cNode D2 = new cNode("D2", 310, 110);

        cNode A3 = new cNode("A3", 10, 210);
        cNode B3 = new cNode("B3", 110, 210);
        cNode C3 = new cNode("C3", 210, 210);
        cNode D3 = new cNode("D3", 310, 210);

        cNode[][] Grid = {{A1, B1, C1, D1}, // orientation
                {A2, B2, C2, D2},
                {A3, B3, C3, D3}
        };

        // Borders
        for (int row=0; row<Grid.length; row++) {
            for (int column=0; column<Grid[row].length; column++) {
                cNode[] Borders = Grid[row][column].findBorders(Grid, row, column);
                Grid[row][column].setBordering(Borders);
            }
        }

        // Array of all states
        cNode[] states = new cNode[Grid.length * Grid[0].length];
        int statex = 0;
        int statey = 0;
        for (int i=0; i<Grid.length * Grid[0].length; i++) {
            if (statex == Grid[0].length) {
                statex = 0;
                statey++;
            }
            states[i] = Grid[statey][statex];
            statex++;
        }

        JFrame fr = new JFrame();
        fr.setBounds(10, 10, 500, 500);
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Colouring algorithm
        boolean coloured = false;
        cNode start_state = states[0];
        for (cNode state : states) { // find state with most borders
            if (state.Bordering.length > start_state.Bordering.length) {
                start_state = state;
            }
        }
        start_state.setColour(map_colours[0]);
        cNode current_state = start_state;

        while(!coloured) {
            if (current_state.UncolouredBordering.length != 0) {
                cNode next_state = current_state.UncolouredBordering[0];
                for (cNode bordering : current_state.UncolouredBordering) {
                    if (bordering.UncolouredBordering.length < current_state.UncolouredBordering.length) {
                        next_state = bordering; // update next state if bordering state has fewer uncoloured borders
                    }
                }
                current_state = next_state;
                current_state.setColour(find_colour(current_state, map_colours));
            } else {
                for (cNode state : states) {
                    if (state.colour == state.defaultColour) {
                        state.setColour(find_colour(state, map_colours)); // Attempt to assign a new colour to the state
                    }
                }
                coloured = true;  // end the while loop
            }
        }


        JPanel pn = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (cNode state : states) {
                    g2.setColor(state.colour);
                    g2.fillOval(state.x, state.y, state.size_x, state.size_y);
                    for (cNode Border : state.Bordering) {

                        int p1_x = state.x+state.size_x/2;
                        int p1_y = state.y+state.size_y/2;

                        int p2_x = p1_x + ((Border.x+Border.size_x/2)-p1_x)/2;
                        int p2_y = p1_y + ((Border.y+Border.size_y/2)-p1_y)/2;
                        Line2D line = new Line2D.Float(p1_x, p1_y, p2_x, p2_y);
                        g2.draw(line);
                    }
                }
            }
        };

        //Title
        JLabel title = new JLabel("3 Coloured graph");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 10, 500, 25);
        title.setForeground(Color.darkGray);
        fr.add(title);

        // Add State Labels (state ids)
        for (cNode state : states) {
            int padAdj = state.size_x/8 * state.id.length();// Padding adjustment depending on id length
            JLabel newLabel = new JLabel(state.id);
            newLabel.setFont(new Font("Arial", Font.BOLD, 20));
            newLabel.setBounds(state.x+state.size_x/2-padAdj, state.y, state.size_x, state.size_y);
            newLabel.setForeground(Color.WHITE);
            fr.add(newLabel);
        }

        fr.add(pn);
        fr.setVisible(true);
        System.out.println("Displayed Successfully.");
    }
}
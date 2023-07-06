import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.ImageIcon;

// Class representing customer information
class CustomerInformation {
    private int custId;
    private String custIC;
    private double counterPaid;
    private int itemsPurchased;

    public CustomerInformation(int custId, String custIC, double counterPaid, int itemsPurchased) {
        this.custId = custId;
        this.custIC = custIC;
        this.counterPaid = counterPaid;
        this.itemsPurchased = itemsPurchased;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public String getCustIC() {
        return custIC;
    }

    public void setCustIC(String custIC) {
        this.custIC = custIC;
    }

    public double getCounterPaid() {
        return counterPaid;
    }

    public void setCounterPaid(double counterPaid) {
        this.counterPaid = counterPaid;
    }

    public int getItemsPurchased() {
        return itemsPurchased;
    }

    public void setItemsPurchased(int itemsPurchased) {
        this.itemsPurchased = itemsPurchased;
    }

    @Override
    public String toString() {
        return "Customer ID: " + custId + "\nItems Purchased: " + itemsPurchased + "\n---------------------------\n";
    }
}

// GUI class
public class HypermarketSelfCheckoutGUI extends JFrame {
    private JButton checkCustomerButton;
    private JTextArea counter1TextArea;
    private JTextArea counter2TextArea;
    private JTextArea counter3TextArea;
    private JTextArea completeStackTextArea;

    public HypermarketSelfCheckoutGUI() {
        initializeWelcomePage();
    }

    // Initialize the welcome page
    private void initializeWelcomePage() {
        setTitle("Welcome to Hypermarket Self Checkout");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Create a panel to hold the components
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BorderLayout(10, 10));

        // Create a label for the welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Hypermarket Self Checkout!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        // Create a label for the image
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\USER\\OneDrive\\Desktop\\download.jpg"); // Replace "path_to_your_image.jpg" with the actual path to your image
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(imageLabel, BorderLayout.CENTER);

        // Create a button to start the checkout process
        checkCustomerButton = new JButton("Check Customer");
        checkCustomerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                remove(welcomePanel);
                initializeGUI();
                processCustomers();
                revalidate();
            }
        });
        welcomePanel.add(checkCustomerButton, BorderLayout.SOUTH);

        container.add(welcomePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Initialize the main GUI
    private void initializeGUI() {
        setTitle("Hypermarket Self Checkout");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        JPanel countersPanel = new JPanel(new GridLayout(1, 3));

        // Text areas to display counter information
        counter1TextArea = new JTextArea();
        counter1TextArea.setEditable(false);
        counter1TextArea.setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane counter1ScrollPane = new JScrollPane(counter1TextArea);
        countersPanel.add(counter1ScrollPane);

        counter2TextArea = new JTextArea();
        counter2TextArea.setEditable(false);
        counter2TextArea.setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane counter2ScrollPane = new JScrollPane(counter2TextArea);
        countersPanel.add(counter2ScrollPane);

        counter3TextArea = new JTextArea();
        counter3TextArea.setEditable(false);
        counter3TextArea.setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane counter3ScrollPane = new JScrollPane(counter3TextArea);
        countersPanel.add(counter3ScrollPane);

        completeStackTextArea = new JTextArea();
        completeStackTextArea.setEditable(false);
        JScrollPane completeStackScrollPane = new JScrollPane(completeStackTextArea);

        container.add(countersPanel, BorderLayout.CENTER);
        container.add(completeStackScrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Process the customers
    private void processCustomers() {
        LinkedList<CustomerInformation> customerList = new LinkedList<>();
        Queue<CustomerInformation> counter1Queue = new LinkedList<>();
        Queue<CustomerInformation> counter2Queue = new LinkedList<>();
        Queue<CustomerInformation> counter3Queue = new LinkedList<>();
        Stack<CustomerInformation> completeStack = new Stack<>();

        // Read customer data from file and populate customerList
        try {
            BufferedReader reader = new BufferedReader(new FileReader("customer_data.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int custId = Integer.parseInt(data[0]);
                String custIC = data[1];
                double counterPaid = Double.parseDouble(data[2]);
                int itemsPurchased = Integer.parseInt(data[3]);

                CustomerInformation customer = new CustomerInformation(custId, custIC, counterPaid, itemsPurchased);
                customerList.add(customer);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int counter1CustomerCount = 0;
        int counter2CustomerCount = 0;

        // Process the customers
        while (!customerList.isEmpty()) {
            CustomerInformation currentCustomer = customerList.removeFirst();

            // If customer has a positive counterPaid value, add them to the completeStack
            if (currentCustomer.getCounterPaid() > 0) {
                completeStack.push(currentCustomer);
                continue;
            }

            // If itemsPurchased is less than or equal to 5, assign the customer to counter1 or counter2
            if (currentCustomer.getItemsPurchased() <= 5) {
                if (counter1CustomerCount < 5) {
                    counter1Queue.add(currentCustomer);
                    counter1CustomerCount++;
                } else {
                    counter2Queue.add(currentCustomer);
                    counter2CustomerCount++;

                    if (counter2CustomerCount == 5) {
                        counter1CustomerCount = 0;
                        counter2CustomerCount = 0;
                    }
                }
            } else {
                // If itemsPurchased is greater than 5, assign the customer to counter3
                counter3Queue.add(currentCustomer);
            }
        }

        // Display information for each counter
        displayCounterInformation(counter1Queue, "1", counter1TextArea);
        displayCounterInformation(counter2Queue, "2", counter2TextArea);
        displayCounterInformation(counter3Queue, "3", counter3TextArea);

        // Display information for each customer in completeStack
        displayCompleteStackInformation(completeStack, completeStackTextArea);
    }

    // Display information for each counter
    private void displayCounterInformation(Queue<CustomerInformation> counterQueue, String counterNumber, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("Counter ").append(counterNumber).append(":\n");
        while (!counterQueue.isEmpty()) {
            CustomerInformation customer = counterQueue.poll();
            sb.append(customer.toString());
        }
        textArea.setText(sb.toString());
    }

    // Display information for each customer in completeStack
    private void displayCompleteStackInformation(Stack<CustomerInformation> completeStack, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        while (!completeStack.isEmpty()) {
            CustomerInformation customer = completeStack.pop();
            sb.append(customer.toString());
        }
        textArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HypermarketSelfCheckoutGUI::new);
    }
}

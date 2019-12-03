import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.net.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class InterfaceGUI extends JFrame{
	SQLExecution sqlHandler;
	private int resultCounter = 0;
	private JPanel container;
	private ResultSet results;
	private JPanel upperTopPanel;
	private String listingName;

	public InterfaceGUI() {
		this.setSize(800,500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sqlHandler = new SQLExecution();
		results = null;


		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,0));
		upperTopPanel = new JPanel();
		JLabel cityListingLabel = new JLabel("You are viewing listings in Austin");
		upperTopPanel.add(cityListingLabel);
		JPanel lowerTopPanel = new JPanel();
		JLabel cityLabel = new JLabel("Select city to search for listings.");
		JButton searchButton = new JButton("Search");
		lowerTopPanel.add(cityLabel);
		JComboBox<String> box = new JComboBox<String>();
		ResultSet cities;


		try {
			results = sqlHandler.makeStatement("Austin");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			cities = sqlHandler.getCities();
			while(cities.next()) {
				box.addItem(cities.getString(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		lowerTopPanel.add(box);
		lowerTopPanel.add(searchButton);

		topPanel.add(upperTopPanel);
		topPanel.add(lowerTopPanel);

		JPanel botPanel = new JPanel();
		JButton nextButton = new JButton("Next Page");
		JButton previousButton = new JButton("Previous");
		botPanel.add(previousButton);
		botPanel.add(nextButton);

		container = new JPanel();
		container.setLayout(new GridLayout(10,2));
		JScrollPane scrPane = new JScrollPane(container);

		scrPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		class previousListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				if(resultCounter > 0) {
					resultCounter--;
					try {
						results = sqlHandler.makeStatement((String) box.getSelectedItem());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for(int i = 0; i < resultCounter * 10; i++) {
						try {
							results.next();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					container.removeAll();
					redrawPanel();
					repaint();
					scrPane.getVerticalScrollBar().setValue(0);
				}else {
					JOptionPane.showMessageDialog(null,
							"You are currently on the first page, press 'Next' to view more results.",
							"Unable to return tp previous page",
							JOptionPane.WARNING_MESSAGE);
				}

			}

		}

		previousButton.addActionListener(new previousListener());

		class nextListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {

				resultCounter++;
				container.removeAll();
				redrawPanel();
				repaint();
				scrPane.getVerticalScrollBar().setValue(0);
			}

		}
		nextButton.addActionListener(new nextListener());

		class searchListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resultCounter = 0;
				cityListingLabel.setText("You are viewing listings in " + (String) box.getSelectedItem());



				container.removeAll();
				try {
					results = sqlHandler.makeStatement((String) box.getSelectedItem());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				redrawPanel();
				scrPane.getVerticalScrollBar().setValue(0);
			}

		}
		searchButton.addActionListener(new searchListener());

		redrawPanel();



		add(scrPane, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
		add(botPanel, BorderLayout.SOUTH);
		this.setVisible(true);


	}



	//This is testing that we are able to print image of listings
	public JLabel addImage(String imageURL) throws SQLException {

		Image image = null;

		try {
			URL url = new URL(imageURL);
			image = ImageIO.read(url);


			Document document;
			try {
				//Get Document object after parsing the html from given url.
				document = Jsoup.connect(results.getString(2)).get();

				//Get title from document object.
				String title = document.title();

				//Print title.

				String[] arr = title.split("-");

				if(arr.length > 2) {
					listingName = arr[0] + arr[1];
				}else {
					listingName = arr[0];
				}

			} catch (IOException e) {
				e.printStackTrace();
			}		


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		image = image.getScaledInstance(300, 300, Image.SCALE_DEFAULT);

		JLabel label = new JLabel(new ImageIcon(image));

		return label;
	}


	public JButton addHyperlink(String listingURL) throws SQLException {

		JButton hyperlink = new JButton("Click to visit listing.");
		hyperlink.setSize(100, 50);
		hyperlink.setForeground(Color.BLUE.darker());
		hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		hyperlink.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					Desktop.getDesktop().browse(new URI(listingURL));

				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// the mouse has entered the label
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// the mouse has exited the label
			}
		});

		return hyperlink;

	}

	public void redrawPanel() {

		try {
			for(int i = resultCounter * 10; i < (resultCounter * 10) + 10; i++) {
				results.next();

				JPanel tempPanel = new JPanel();
				JPanel rightPanel = new JPanel();
				JPanel leftPanel = new JPanel();
				leftPanel.setLayout(new GridLayout(2,0));

				JPanel botLeftPanel = new JPanel();



				try {
					rightPanel.add(addImage(results.getString(3)));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//Neighborhood actually contains the listingName

				JLabel neighborhood = new JLabel(listingName);

				//neighborhood.setPreferredSize(new Dimension(250,100));
				neighborhood.setMinimumSize(new Dimension(500,100));
				leftPanel.add(neighborhood, BorderLayout.NORTH);

				try {
					botLeftPanel.add(addHyperlink(results.getString(2)));

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JLabel safety = new JLabel("Safety Score");
				botLeftPanel.add(safety);
				leftPanel.add(botLeftPanel);


				tempPanel.add(leftPanel);
				tempPanel.add(rightPanel);

				tempPanel.setBorder(new EtchedBorder());
				container.add(tempPanel);


			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}

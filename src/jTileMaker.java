import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import java.awt.ComponentOrientation;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JRadioButton;

public class jTileMaker extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7837680618388541839L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					jTileMaker frame = new jTileMaker();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private int iTileSize=32, iDefaultTileSize=32;
	private int iTileMax=1;
	private int iTileIndex=0;
	private int iZoom=10;
	private int iPointSize=1;
	private int iMaxUndo=0;
	private int iCurrentUndo=0;
	private int xOffset=0;
	private int iGradSize=32;

	private String sLastTileSet="";
	private String sLastTilePath="";
	private String sUndoPath="";
	private boolean blIsChanged=false;
	private BufferedImage imgTileSet=null;
	private BufferedImage imgCanvas=null;
	private BufferedImage imgGrid=null;
	private BufferedImage imgThisTile=null;
	private JPanel TileStrip;
	private JScrollPane scrollPaneStrip;
	private JPanel ViewPane;
	private JPanel StatusBar;
	private JLabel lbZoom;
	private JLabel lbGridPos;
	private JLabel lbMousePos;
	private JPanel pnlPalette;
	private JPanel pnlLeftColor,pnlRightColor;
	private JPanel pnlToolbox;
	private JScrollPane scrollPane;
	private GradientMaker gmGradient;
	private ArrayList<JPanel> alPalette = new ArrayList<JPanel>();
	
	private int iGridX=-1,iGridY=-1;
	private Color clLeftclick=Color.BLACK, clRightclick=Color.WHITE;
	private String DesktopDir;
	private static int MAXIMUM_ZOOM=50;
	private static int MINIMUM_ZOOM=5;//grid is switched off when zoom < minimum_zoom
	private boolean blShowGrid=false;
	private boolean blSquareBrush=false;
	private boolean blIsNewSelection=true;
	private boolean blFilled=true;
	private boolean blUseGradient=true;
	private boolean blDragging=false;
	private Point ptStartDrag;
	private Point ptLastPos;
	private String APP_PATH;
	private JPanel pnlTop;
	private JPanel btnGradient;
	private JToggleButton  btnLine;
	private JToggleButton  btnCircle;
	private JToggleButton  btnRect;
	private JToggleButton  btnFill;
	private JTextField txtPointSize;
	private enum DrawMode {
	    NONE,DRAW,LINE,CIRCLE,RECT,FILL 
	}
	private DrawMode CurrentDrawMode=DrawMode.DRAW;
	private int CurrentGradientDrawMode=0;
	private JLabel lblNewLabel;
	private JMenu mnEdit;
	private JMenuItem mntmUndo;
	private JMenuItem mntmRedo;
	private JTextField txtGradLength;
	private JCheckBox cbxGridLines;
	/**
	 * Create the frame.
	 */
	public jTileMaker() {
		setTitle("Tile Maker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 614);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewTileSet = new JMenuItem("New TileSet");
		mntmNewTileSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try
				{
					int size = Integer.parseInt(JOptionPane.showInputDialog("Enter tile size:"));
					if (size>0 && size<=256)
					{
						NewTileSet(size);
					}
					else
					{
						iTileSize=iDefaultTileSize;
					}
				}catch(Exception e){e.printStackTrace();}
			}
		});
		mnFile.add(mntmNewTileSet);
		
		JMenuItem mntmOpenTileSet = new JMenuItem("Open TileSet");
		mntmOpenTileSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LoadTileSet();
			}
		});
		mnFile.add(mntmOpenTileSet);
		
		JMenuItem mntmSaveTileSet = new JMenuItem("Save TileSet");
		mntmSaveTileSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SaveTileSet();
			}
		});
		mnFile.add(mntmSaveTileSet);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		mntmUndo = new JMenuItem("Undo");
		mntmUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LoadTempImg();
			}
		});
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mnEdit.add(mntmUndo);
		
		mntmRedo = new JMenuItem("Redo");
		mntmRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iCurrentUndo+=2;
				LoadTempImg();
			}
		});
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnEdit.add(mntmRedo);
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(800, 600));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		ViewPane = new JPanel()		
		{		
			/**
			 * 
			 */
			private static final long serialVersionUID = -1066682914200045575L;

			@Override
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
				if(imgCanvas!=null)
				{
					g.drawImage(imgCanvas, 0, 0, null);
					
					if(blShowGrid && imgGrid!=null)
					{
						//Todo : large images make the drawimage below freeze the app
						g.setXORMode(Color.black);
						g.drawImage(imgGrid, 0, 0, null);
					}
					//cursor:
					if(iGridX>=0 && iGridY>=0 && iGridX<iTileSize && iGridY<iTileSize)
					{
						g.setXORMode(Color.white);
						g.fillOval(iGridX*iZoom, iGridY*iZoom, iZoom, iZoom);
					}
					g.setPaintMode();
				}
			}
		};
		ViewPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) 
			{
				blDragging = false;

				Color clDrawColor = Color.black;
				if(e.getButton() == MouseEvent.BUTTON1)
				{
					clDrawColor = clLeftclick;
				}
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					clDrawColor = clRightclick;
				}
				Point ptMouse = e.getPoint();
				iGridX = Math.min((int)ptMouse.getX()/iZoom,iTileSize-1);//which grid square ?
				iGridY = Math.min((int)ptMouse.getY()/iZoom,iTileSize-1);
				switch(CurrentDrawMode)
				{
					case DRAW:
					{
						DrawAtMouse(gmGradient.GetNextColor());	
						break;
					}
					case LINE:
					{

						DrawSelectionLine(true,clDrawColor);	
						break;
					}
					case CIRCLE:
					{

						DrawSelectionCircle(true,clDrawColor);	
						break;
					}
					case RECT:
					{

						DrawSelectionRect(true,clDrawColor);
						break;
					}
					case FILL:
					{
						//GradientFloodFill(imgThisTile, new Point(iGridX,iGridY));
						if(blUseGradient)
						{
							QueueFloodfill(imgThisTile, new Point(iGridX,iGridY), clDrawColor, true);
						}
						else
						{
							QueueFloodfill(imgThisTile, new Point(iGridX,iGridY), clDrawColor, false);
						}
							
						DrawTileOnStrip();
						TileStrip.repaint();
						Graphics g = imgCanvas.getGraphics();
						g.drawImage(imgThisTile, 0, 0, iTileSize*iZoom, iTileSize*iZoom, null);		

						break;
					}
					case NONE:default:
					{
						
					}				
				};
				ViewPane.repaint();

				ptLastPos = new Point(iGridX,iGridY);	
				SaveTempImg();
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				blDragging = true;
				blIsNewSelection = true;
				Point ptMouse = arg0.getPoint();
				iGridX = (int)ptMouse.getX()/iZoom;//which grid square ?
				iGridY = (int)ptMouse.getY()/iZoom;
				ptLastPos = new Point(iGridX,iGridY);
				ptStartDrag = new Point(iGridX,iGridY);
				gmGradient.Reset();
				gmGradient.SetSteps(iGradSize);
			}
		});
		ViewPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				Point ptMouse = arg0.getPoint();
				int iOldX = iGridX;
				int iOldY = iGridY;
				iGridX = Math.min((int)ptMouse.getX()/iZoom,iTileSize-1);//which grid square ?
				iGridY = Math.min((int)ptMouse.getY()/iZoom,iTileSize-1);
				if(!(iOldX==iGridX && iOldY==iGridY))
				{
					ViewPane.repaint();
					lbGridPos.setText("Grid Position x:"+iGridX+", y:"+iGridY);
				}
			}
			@Override
			public void mouseDragged(MouseEvent arg0) {
				Point ptMouse = arg0.getPoint();
				int iOldX = iGridX;
				int iOldY = iGridY;
				iGridX = Math.min((int)ptMouse.getX()/iZoom,iTileSize-1);//which grid square ?
				iGridY = Math.min((int)ptMouse.getY()/iZoom,iTileSize-1);
				//only draw if coords changed
				if(!(iOldX==iGridX && iOldY==iGridY))
				{
					int b1 = MouseEvent.BUTTON1_DOWN_MASK;
					int b2 = MouseEvent.BUTTON3_DOWN_MASK;
					Color clDrawColor = Color.black;
					if ((arg0.getModifiersEx() & (b1 | b2)) == b1)
					{
						clDrawColor = clLeftclick;
					}
					else if ((arg0.getModifiersEx() & (b1 | b2)) == b2)
					{
						clDrawColor = clRightclick;
					}
					switch(CurrentDrawMode)
					{
						case DRAW:
						{
							
							DrawAtMouse(blUseGradient?gmGradient.GetNextColor():clDrawColor);	
							break;
						}
						case LINE:
						{
							DrawSelectionLine(false,clDrawColor);	
							break;
						}
						case CIRCLE:
						{
							DrawSelectionCircle(false,clDrawColor);		
							break;
						}
						case RECT:
						{
							DrawSelectionRect(false,clDrawColor);		
							break;
						}
		
						case NONE:default:
						{
							
						}	
					};
					ViewPane.repaint();
				}
				ptLastPos = new Point(iGridX,iGridY);
			}
		});
		ViewPane.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int clicks = arg0.getWheelRotation();
				if (clicks < 0)//up
				{
					if(iZoom>1)
					{
						iZoom--;
					}
					if (iZoom<=MINIMUM_ZOOM)
					{
						blShowGrid=false;
					};
					Zoom();
				}
				else if (clicks>0)
				{
					if (iZoom<MAXIMUM_ZOOM)iZoom++;
					if(iZoom>MINIMUM_ZOOM)
					{
						blShowGrid=cbxGridLines.isSelected();
					}
					Zoom();
				};
				setStatusBar();			
			}
		});
;
		scrollPane.setViewportView(ViewPane);
		GroupLayout gl_ViewPane = new GroupLayout(ViewPane);
		gl_ViewPane.setHorizontalGroup(
			gl_ViewPane.createParallelGroup(Alignment.LEADING)
				.addGap(0, 598, Short.MAX_VALUE)
		);
		gl_ViewPane.setVerticalGroup(
			gl_ViewPane.createParallelGroup(Alignment.LEADING)
				.addGap(0, 431, Short.MAX_VALUE)
		);
		ViewPane.setLayout(gl_ViewPane);
		
		StatusBar = new JPanel();
		StatusBar.setPreferredSize(new Dimension(800, 30));
		
		pnlTop = new JPanel();
		pnlTop.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlTop.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlTop.setMinimumSize(new Dimension(32, 70));

		scrollPaneStrip = new JScrollPane();
		scrollPaneStrip.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPaneStrip.setMinimumSize(new Dimension(23, 32));

		scrollPaneStrip.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		TileStrip = new JPanel()
		{		
			/**
			 * 
			 */
			private static final long serialVersionUID = -1950729699552115915L;

			@Override
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
				if(imgTileSet!=null)
				{
					g.drawImage(imgTileSet, 0, 0, null);
					if(iTileIndex>=0)
					{
						g.setColor(Color.RED);
						g.drawRect(iTileSize*iTileIndex, 0, iTileSize, iTileSize);
					}
				}
			}
		};
		TileStrip.setMinimumSize(new Dimension(32, 32));
		TileStrip.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Point pt = e.getPoint();
				int index = (int)pt.getX()/iTileSize;
				if(index>=0&&index<iTileMax)
				{
					iTileIndex = index;
				}
				xOffset = iTileIndex * iTileSize;
				TileStrip.repaint();
				SelectTile();
				Zoom();
			}
		});
		scrollPaneStrip.setViewportView(TileStrip);
		
		JButton btnNewTile = new JButton("+");
		btnNewTile.setBorder(null);
		btnNewTile.setBorderPainted(false);
		btnNewTile.setMargin(new Insets(0,0,0,0));
		btnNewTile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CreateNewTile();				
			}
		});
		btnNewTile.setToolTipText("Create a New Tile");
		
		JButton btnDeleteTile = new JButton("-");
		btnDeleteTile.setToolTipText("Create a New Tile");
		btnDeleteTile.setMargin(new Insets(0, 0, 0, 0));
		btnDeleteTile.setBorderPainted(false);
		btnDeleteTile.setBorder(null);
		GroupLayout gl_pnlTop = new GroupLayout(pnlTop);
		gl_pnlTop.setHorizontalGroup(
			gl_pnlTop.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlTop.createSequentialGroup()
					.addGroup(gl_pnlTop.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewTile, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDeleteTile, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPaneStrip, GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE))
		);
		gl_pnlTop.setVerticalGroup(
			gl_pnlTop.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_pnlTop.createSequentialGroup()
					.addGroup(gl_pnlTop.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPaneStrip, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_pnlTop.createSequentialGroup()
							.addComponent(btnNewTile, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnDeleteTile, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		pnlTop.setLayout(gl_pnlTop);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		lbZoom = new JLabel("New label");
		
		lbGridPos = new JLabel("New label");
		
		lbMousePos = new JLabel("New label");
		GroupLayout gl_StatusBar = new GroupLayout(StatusBar);
		gl_StatusBar.setHorizontalGroup(
			gl_StatusBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_StatusBar.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbGridPos)
					.addGap(18)
					.addComponent(lbMousePos)
					.addGap(18)
					.addComponent(lbZoom)
					.addGap(648))
		);
		gl_StatusBar.setVerticalGroup(
			gl_StatusBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_StatusBar.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_StatusBar.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbGridPos)
						.addComponent(lbMousePos)
						.addComponent(lbZoom)))
		);
		StatusBar.setLayout(gl_StatusBar);
		contentPane.add(StatusBar, BorderLayout.PAGE_END);
		
		pnlToolbox = new JPanel();
		pnlToolbox.setLocation(new Point(0, 100));
		pnlToolbox.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		pnlLeftColor = new JPanel();
		pnlLeftColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				clLeftclick = JColorChooser.showDialog(null, "Choose a color", pnlLeftColor.getBackground());
				pnlLeftColor.setBackground(clLeftclick);
				pnlLeftColor.repaint();
				UpdateGradient();
				AddToPalette(clLeftclick);
			}
		});
		pnlLeftColor.setBackground(Color.BLACK);
		
		pnlRightColor = new JPanel();
		pnlRightColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				clRightclick = JColorChooser.showDialog(null, "Choose a color", pnlRightColor.getBackground());
				pnlRightColor.setBackground(clRightclick);		
				pnlRightColor.repaint();
				UpdateGradient();
				AddToPalette(clRightclick);				
			}
		});
		pnlRightColor.setForeground(Color.BLACK);
		pnlRightColor.setBackground(Color.WHITE);
		
		cbxGridLines = new JCheckBox("Gridlines");
		cbxGridLines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(blShowGrid!=cbxGridLines.isSelected())
				{
					blShowGrid = !blShowGrid;
					Zoom();
				};
			}
		});
		
		pnlPalette = new JPanel();
		pnlPalette.setPreferredSize(new Dimension(250, 10));
		pnlPalette.setMaximumSize(new Dimension(250, 32767));
		pnlPalette.setBackground(Color.WHITE);
		ButtonGroup bgrp = new ButtonGroup();
		
		JToggleButton btnDraw = new JToggleButton("Draw");
		btnDraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CurrentDrawMode=DrawMode.DRAW;
			}
		});
		btnDraw.setSelected(true);
		btnDraw.setMargin(new Insets(2, 2, 2, 2));
		bgrp.add(btnDraw);
		
		btnLine = new JToggleButton("Line");
		btnLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CurrentDrawMode=DrawMode.LINE;
			}
		});
		btnLine.setMargin(new Insets(2, 2, 2, 2));
		bgrp.add(btnLine);
		
		btnCircle = new JToggleButton("Circle");
		btnCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CurrentDrawMode=DrawMode.CIRCLE;
			}
		});
		btnCircle.setMargin(new Insets(2, 0, 2, 0));
		bgrp.add(btnCircle);
		
		btnRect = new JToggleButton("Rect");
		btnRect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CurrentDrawMode=DrawMode.RECT;
			}
		});
		btnRect.setMargin(new Insets(2, 2, 2, 2));
		bgrp.add(btnRect);
		
		btnFill = new JToggleButton("Fill");
		btnFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CurrentDrawMode=DrawMode.FILL;
			}
		});
		btnFill.setMargin(new Insets(2, 2, 2, 2));
		bgrp.add(btnFill);
		
		
		txtPointSize = new JTextField();
		txtPointSize.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int iChange = arg0.getWheelRotation();
				if(iPointSize-iChange>0)
				{
					iPointSize-=iChange;
				}
				txtPointSize.setText( Integer.toString(iPointSize));	
			}
		});
		txtPointSize.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		txtPointSize.setText("1");
		txtPointSize.setColumns(10);
		
		lblNewLabel = new JLabel("Size:");
		
		JCheckBox chckbxFilled = new JCheckBox("Filled");
		chckbxFilled.setSelected(true);
		chckbxFilled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				blFilled = chckbxFilled.isSelected();
			}
		});
		
		btnGradient = new JPanel()
		{		
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * 
			 */

			@Override
			protected void paintComponent(Graphics g) 
			{
				super.paintComponent(g);
				
				if(gmGradient!=null)
				{
					GradientMaker gmNew = gmGradient.Copy();
					int height = getHeight();
					int width = getWidth();
					gmNew.SetSteps(width);

					for(int x=0;x<width;x++)
					{
						g.setColor(gmNew.GetNextColor());
						g.drawLine(x, 0, x, height);
					}
				}
			}
		};
		btnGradient.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				CurrentGradientDrawMode=(CurrentGradientDrawMode+1)%4;
				UpdateGradient();
			}
		});
		ButtonGroup bg = new ButtonGroup();
		JRadioButton rbtSquare = new JRadioButton("Square");
		rbtSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				blSquareBrush = rbtSquare.isSelected();
			}
		});
		bg.add(rbtSquare);
		JRadioButton rbtRound = new JRadioButton("Round");
		rbtRound.setSelected(true);
		rbtRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blSquareBrush = !rbtRound.isSelected();
			}
		});
		bg.add(rbtRound);
		
		JCheckBox chkGradient = new JCheckBox("Gradient");
		chkGradient.setSelected(true);
		chkGradient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blUseGradient = chkGradient.isSelected();
			}
		});
		
		txtGradLength = new JTextField();
		txtGradLength.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int iChange = arg0.getWheelRotation();
				if(iGradSize-iChange>0)
				{
					iGradSize-=iChange;
				}
				txtGradLength.setText( Integer.toString(iGradSize));	
			}
		});
		txtGradLength.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		txtGradLength.setText("32");
		txtGradLength.setColumns(10);
		

		GroupLayout gl_pnlToolbox = new GroupLayout(pnlToolbox);
		gl_pnlToolbox.setHorizontalGroup(
			gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlToolbox.createSequentialGroup()
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
						.addComponent(btnGradient, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
						.addGroup(gl_pnlToolbox.createSequentialGroup()
							.addComponent(pnlLeftColor, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
							.addComponent(pnlRightColor, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblNewLabel)
						.addComponent(pnlPalette, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
						.addGroup(gl_pnlToolbox.createSequentialGroup()
							.addComponent(txtPointSize, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rbtSquare)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rbtRound, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_pnlToolbox.createSequentialGroup()
							.addComponent(cbxGridLines)
							.addPreferredGap(ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
							.addComponent(chckbxFilled))
						.addGroup(gl_pnlToolbox.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnDraw, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnLine, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
							.addGap(11)
							.addComponent(btnRect, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_pnlToolbox.createSequentialGroup()
							.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_pnlToolbox.createSequentialGroup()
									.addContainerGap()
									.addComponent(btnFill, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
								.addComponent(chkGradient))
							.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_pnlToolbox.createSequentialGroup()
									.addGap(37)
									.addComponent(btnCircle, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_pnlToolbox.createSequentialGroup()
									.addGap(18)
									.addComponent(txtGradLength, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)))))
					.addGap(71))
		);
		gl_pnlToolbox.setVerticalGroup(
			gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlToolbox.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPointSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(rbtSquare)
						.addComponent(rbtRound))
					.addGap(18)
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbxGridLines)
						.addComponent(chckbxFilled))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
						.addComponent(btnRect)
						.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnDraw)
							.addComponent(btnLine)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnFill)
						.addComponent(btnCircle))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.BASELINE)
						.addComponent(chkGradient)
						.addComponent(txtGradLength, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_pnlToolbox.createParallelGroup(Alignment.LEADING)
						.addComponent(pnlLeftColor, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlRightColor, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnGradient, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
					.addComponent(pnlPalette, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE))
		);
		pnlToolbox.setLayout(gl_pnlToolbox);
		contentPane.add(pnlToolbox, BorderLayout.LINE_START);

		pnlPalette.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(pnlTop, BorderLayout.PAGE_START);
		pack();
		Initialise();
	}
	private void SaveTileSet()
	{
		if(blIsChanged)
		{
			JFileChooser fileChooser = new JFileChooser(DesktopDir);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Bitmap Files", "bmp");
			fileChooser.setFileFilter(filter);
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
			{
				File file = fileChooser.getSelectedFile();
				String fname = file.getName();
				int iPosExtension = fname.lastIndexOf('.');
				if (!fname.endsWith("bmp"))
				{
					if (iPosExtension>0)
					{
						fname = fname.substring(0, iPosExtension);
					}
				    file = new File(file.toString() + ".bmp");
				}
				SetFileName(file.getAbsolutePath());
				sLastTileSet = file.getName();
				sUndoPath=file.getAbsolutePath()+"//Undo//";
				setTitle("Tile Maker : "+sLastTileSet);
				try {
				ImageIO.write(imgTileSet, "bmp", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  // save to file
				SaveProperties();
			}	
		}
	}
	private void SaveProperties()
	{
		// Save Settings
	    Properties saveProps = new Properties();
	    saveProps.setProperty("Last Tile Set", sLastTilePath+"\\"+sLastTileSet);
    	try {
				saveProps.storeToXML(new FileOutputStream(APP_PATH+"\\Config\\settings.xml"), "");
			} catch (FileNotFoundException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	private void LoadTileSet()
	{
		JFileChooser fileChooser = new JFileChooser(DesktopDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Bitmap Files", "bmp");
		fileChooser.setFileFilter(filter);

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{
			File file = fileChooser.getSelectedFile();
			LoadTileSet(file.toString());
			SaveProperties();
			// load from file
		}
		
	}
	private void LoadTileSet(String sFile)
	{
		try
		{
			File file = new File(sFile);
			imgTileSet = ImageIO.read(file);			
		}
		catch(IOException e)
		{
			return;
		};
		ClearUndo();
		SetFileName(sFile);
		iTileSize = imgTileSet.getHeight();
		iTileMax = imgTileSet.getWidth()/iTileSize;
		iTileIndex=0;
		xOffset = iTileIndex * iTileSize;
		SelectTile();
		ResizeTilestrip();
		TileStrip.repaint();
		Zoom();

	}
	private void LoadProperties()
	{
	    // Load Settings
	    Properties loadProps = new Properties();
	    try {
	    		loadProps.loadFromXML(new FileInputStream(APP_PATH+"\\Config\\settings.xml"));
			} catch (InvalidPropertiesFormatException e) 
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) 
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    sLastTileSet = loadProps.getProperty("Last Tile Set");
	    if(sLastTileSet!=null)
	    {
		    setTitle(sLastTileSet);
	    	LoadTileSet(sLastTileSet);
	    }
	    //String path2 = loadProps.getProperty("path2");
	}
	private void NewTileSet(int iSize)
	{
		iTileSize=iSize;
		iTileMax=1;
		iTileIndex=0;
		xOffset = iTileIndex * iTileSize;
		ClearUndo();
		SetFileName(APP_PATH+"temp\\tempImg.bmp");
		//new image, paint it white
		imgTileSet = new BufferedImage(iSize, iSize,  BufferedImage.TYPE_INT_RGB);
		Graphics g = imgTileSet.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, iTileSize, iTileSize);
		ResizeTilestrip();
		SelectTile();
		Zoom();
		blIsChanged=true;
		SaveTempImg();
	}
	public void PaintGrid()
	{
		if(blShowGrid && imgGrid!=null)
		{
			Graphics g = imgGrid.getGraphics();
			g.setColor(Color.white);
			for(int y=0;y<=iTileSize;y++)
			{
				g.drawLine(0, y*iZoom, iTileSize*iZoom, y*iZoom);
			}
			for(int x=0;x<iTileSize;x++)
			{
				g.drawLine(x*iZoom, 0,  x*iZoom, iTileSize*iZoom);
			}
		}
	}
	public void setStatusBar()
	{
		lbZoom.setText("Zoom : "+iZoom+"00%");
		lbGridPos.setText("Grid Position x:"+iGridX+", y:"+iGridY);
	}
	public void Zoom()
	{
		imgCanvas = new BufferedImage(iTileSize*iZoom,iTileSize*iZoom,BufferedImage.TYPE_INT_RGB);
		imgGrid = new BufferedImage(iTileSize*iZoom,iTileSize*iZoom,BufferedImage.TYPE_INT_RGB);
		
		//Graphics g = imgCanvas.getGraphics();
		//g.drawImage(imgTileSet, 0, 0, iTileSize*iZoom, iTileSize*iZoom,xOffset,0,xOffset+iTileSize, iTileSize, null);
		Graphics g = imgCanvas.getGraphics();
		//g.drawImage(imgThisTile, 0, 0, iTileSize*iZoom, iTileSize*iZoom,0,0,iTileSize, iTileSize, null);
		g.drawImage(imgThisTile, 0, 0, iTileSize*iZoom, iTileSize*iZoom,null);
		ViewPane.setPreferredSize(new Dimension(imgCanvas.getWidth(),imgCanvas.getHeight()));
		PaintGrid();
		ViewPane.repaint();
	}
	public void DrawAtMouse(Color cl)
	{
		//draw on tilestrip:
		Graphics g = imgThisTile.getGraphics();
		g.setColor(cl);
		//this line is actually just a point
		g.setPaintMode();
		
		if(ptLastPos==null)
		{
			g.drawLine(iGridX,iGridY,iGridX,iGridY);
		}
		else
		{
			g.drawLine(iGridX,iGridY,(int)ptLastPos.getX(),(int)ptLastPos.getY());
		}
		int iBrushSize = Integer.parseInt(txtPointSize.getText());
		if(blSquareBrush)
		{
			g.fillRect(iGridX-(iBrushSize/2), iGridY-(iBrushSize/2),iBrushSize,iBrushSize);
		}
		else
		{
			g.fillOval(iGridX-(iBrushSize/2), iGridY-(iBrushSize/2),iBrushSize,iBrushSize);			
		}
		TileStrip.repaint();
		DrawTileOnStrip();
		//draw on Canvas

		g = imgCanvas.getGraphics();
		g.drawImage(imgThisTile, 0, 0, iTileSize*iZoom, iTileSize*iZoom, null);
		ViewPane.repaint();	
	}
	public void Initialise()
	{
		DesktopDir = System.getProperty("user.home")+"/Desktop";
		APP_PATH = new File(".").getAbsolutePath();
		APP_PATH = APP_PATH.substring(0,APP_PATH.length()-1);
		File theDir = new File(APP_PATH+"Config");
		theDir.mkdir();
		LoadProperties();
		//create basic default palette
		AddToPalette(Color.BLACK);
		AddToPalette(Color.GRAY);
		AddToPalette(Color.WHITE);
		AddToPalette(Color.RED);
		AddToPalette(Color.ORANGE);
		AddToPalette(Color.YELLOW);
		AddToPalette(Color.GREEN);
		AddToPalette(Color.BLUE);
		AddToPalette(Color.CYAN);
		AddToPalette(Color.MAGENTA);
		//JOptionPane.showMessageDialog(null, "App path set to :"+APP_PATH);
		gmGradient = new GradientMaker(clLeftclick,clRightclick,100,false);
	}
	public void CreateNewTile()
	{
		iTileIndex = iTileMax++;

		BufferedImage imgNew = new BufferedImage(iTileSize*iTileMax, iTileSize,  BufferedImage.TYPE_INT_RGB);
		//set index to last
		Graphics g = imgNew.getGraphics();
		g.drawImage(imgTileSet, 0, 0, null);
		g.fillRect(iTileSize*(iTileMax-1), 0, iTileSize*iTileMax, iTileSize);
		imgTileSet = imgNew;
		TileStrip.repaint();
		xOffset = iTileIndex * iTileSize;

		SelectTile();
		Zoom();
		blIsChanged=true;	
		SaveTempImg();
	}
	private void AddToPalette(Color cl)
	{
		JPanel pnlNew = new JPanel();
		pnlNew.setBackground(cl);
		pnlNew.setPreferredSize(new Dimension(20,20));
		pnlNew.setBorder(BorderFactory.createLineBorder(Color.black));
		pnlNew.setVisible(true);
		pnlNew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int iBtn = arg0.getButton();
				if(iBtn == MouseEvent.BUTTON1)
				{
					clLeftclick = cl;
					pnlLeftColor.setBackground(cl);
					pnlLeftColor.repaint();
				}
				else if (iBtn == MouseEvent.BUTTON3)
				{
					clRightclick = cl;
					pnlRightColor.setBackground(cl);
					pnlRightColor.repaint();
				}
				blDragging=false;
				UpdateGradient();
			}
		});
		pnlPalette.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
		alPalette.add(pnlNew);
		//pnlNew.setLayout(new GridLayout(1, 0, 0, 0));
		pnlPalette.add(pnlNew);
		pnlPalette.revalidate();
		pnlPalette.repaint();
	}
	private void DrawSelectionLine(boolean blFinal, Color clDraw)
	{
		Graphics g = imgThisTile.getGraphics();
		g.setXORMode(Color.gray);
		//remove old
		if(!blIsNewSelection)
		{
			g.drawLine((int)ptStartDrag.getX(),(int)ptStartDrag.getY(),(int)ptLastPos.getX(),(int)ptLastPos.getY());
		}
		blIsNewSelection = false;
		//draw new
		if(blFinal)
		{
			g.setPaintMode();
			g.setColor(clDraw);
		}
		g.drawLine((int)ptStartDrag.getX(),(int)ptStartDrag.getY(),iGridX,iGridY);
		DrawTileOnStrip();
		TileStrip.repaint();
		g = imgCanvas.getGraphics();
		g.drawImage(imgTileSet, 0, 0, iTileSize*iZoom, iTileSize*iZoom,xOffset,0,xOffset+iTileSize, iTileSize, null);		
	}
	private void DrawSelectionRect(boolean blFinal, Color clDraw)
	{
		Graphics g = imgThisTile.getGraphics();
		g.setXORMode(Color.gray);
		//remove old rect
		if(!blIsNewSelection)
		{
			if(blFilled)
			{
				g.fillRect((int)Math.min(ptStartDrag.getX(),ptLastPos.getX()), (int)Math.min(ptStartDrag.getY(),ptLastPos.getY()), (int)Math.abs((ptLastPos.getX() - ptStartDrag.getX())), (int)Math.abs((ptLastPos.getY() - ptStartDrag.getY())));
			}
			else
			{
				g.drawRect((int)Math.min(ptStartDrag.getX(),ptLastPos.getX()), (int)Math.min(ptStartDrag.getY(),ptLastPos.getY()), (int)Math.abs((ptLastPos.getX() - ptStartDrag.getX())), (int)Math.abs((ptLastPos.getY() - ptStartDrag.getY())));							
			}
		}
		blIsNewSelection = false;
		//draw new
		if(blFinal)
		{
			g.setPaintMode();
			g.setColor(clDraw);
		}
		if(blFilled)
		{
			g.fillRect((int)Math.min(ptStartDrag.getX(),iGridX), (int)Math.min(ptStartDrag.getY(),iGridY), (int)Math.abs((iGridX - ptStartDrag.getX())), (int)Math.abs((iGridY - ptStartDrag.getY())));
		}
		else
		{
			g.drawRect((int)Math.min(ptStartDrag.getX(),iGridX), (int)Math.min(ptStartDrag.getY(),iGridY), (int)Math.abs((iGridX - ptStartDrag.getX())), (int)Math.abs((iGridY - ptStartDrag.getY())));			
		}
		DrawTileOnStrip();
		TileStrip.repaint();
		g = imgCanvas.getGraphics();
		g.drawImage(imgTileSet, 0, 0, iTileSize*iZoom, iTileSize*iZoom,xOffset,0,xOffset+iTileSize, iTileSize, null);
	}
	private void DrawSelectionCircle(boolean blFinal, Color clDraw)
	{
		Graphics g = imgThisTile.getGraphics();
		g.setXORMode(Color.gray);
		//remove old
		if(!blIsNewSelection)
		{
			int iWidth = (int)Math.abs((ptLastPos.getX() - ptStartDrag.getX()));
			int iHeight = (int)Math.abs((ptLastPos.getY() - ptStartDrag.getY()));
			if(blFilled)
			{
				g.fillOval((int)Math.min(ptStartDrag.getX(),ptLastPos.getX())-iWidth, (int)Math.min(ptStartDrag.getY(),ptLastPos.getY())-iHeight, iWidth * 2, iHeight * 2);
			}
			else
			{
				g.drawOval((int)Math.min(ptStartDrag.getX(),ptLastPos.getX())-iWidth, (int)Math.min(ptStartDrag.getY(),ptLastPos.getY())-iHeight, iWidth * 2, iHeight * 2);
			}
		}
		blIsNewSelection = false;
		//draw new
		if(blFinal)
		{
			g.setPaintMode();
			g.setColor(clDraw);
		}
		int iWidth = (int)Math.abs((iGridX - ptStartDrag.getX()));
		int iHeight = (int)Math.abs((iGridY - ptStartDrag.getY()));
		if(blFilled)
		{
			g.fillOval((int)Math.min(ptStartDrag.getX(),iGridX)-iWidth, (int)Math.min(ptStartDrag.getY(),iGridY)-iHeight, iWidth * 2, iHeight * 2);
		}
		else
		{
			g.drawOval((int)Math.min(ptStartDrag.getX(),iGridX)-iWidth, (int)Math.min(ptStartDrag.getY(),iGridY)-iHeight, iWidth * 2, iHeight * 2);
		}
		DrawTileOnStrip();
		TileStrip.repaint();
		g = imgCanvas.getGraphics();
		g.drawImage(imgTileSet, 0, 0, iTileSize*iZoom, iTileSize*iZoom,xOffset,0,xOffset+iTileSize, iTileSize, null);		
	}
	private void SaveTempImg()
	{
		iCurrentUndo++;
		mntmUndo.setEnabled(true);
		mntmRedo.setEnabled(iCurrentUndo<iMaxUndo);
		//create temp folder:
		String sFilename = sLastTileSet.replace(".", "_");
		File theDir=new File(sUndoPath);
		theDir.mkdir();
		File file = new File(sUndoPath+sFilename+Integer.toString(iCurrentUndo)+".bmp");
		try
		{
			ImageIO.write(imgTileSet, "bmp", file);
		} 
		catch(IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iMaxUndo = Math.max(iCurrentUndo, iMaxUndo);
	}
	private void LoadTempImg()
	{
		iCurrentUndo--;
		mntmUndo.setEnabled(iCurrentUndo>0);
		mntmRedo.setEnabled(true);
		//create temp folder:
		String sFilename = sLastTileSet.replace(".", "_");
		//if iCurrentUndo reaches 0, then load original image instead of a temp.
		String theDir = (iCurrentUndo>0)
				?sUndoPath+sFilename+Integer.toString(iCurrentUndo)+".bmp"
				:sLastTilePath+"\\"+sLastTileSet;
		File file = new File(theDir);
		
		try
		{
			imgTileSet = ImageIO.read(file);			
		}
		catch(IOException e)
		{
			return;
		};
		iTileMax = imgTileSet.getWidth() / imgTileSet.getHeight();
		if(iTileIndex>=iTileMax)
		{
			iTileIndex = 0;
		}
		xOffset = iTileIndex * iTileSize;

		SelectTile();
		ResizeTilestrip();
		TileStrip.repaint();
		Zoom();	
	}
	private void SetFileName(String sFullPath)
	{
		int iPosPath = sFullPath.lastIndexOf('\\');
		sLastTileSet=sFullPath.substring(iPosPath+1,sFullPath.length());
		sLastTilePath=sFullPath.substring(0,iPosPath);
		String sFilename = sLastTileSet.replace(".", "_");
		sUndoPath=sLastTilePath+"\\"+sFilename+"\\";
		setTitle(sFullPath);
	}
	private void ClearUndo()
	{
		//delete the undo folder and reset markers
		iMaxUndo=0;
		iCurrentUndo=0;
		//TODO : delete last undo folder:
		File theDir=new File(sUndoPath);
		try
		{
			deleteDirectory(theDir);
		}
		catch(Exception e){}
	}
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	private void UpdateGradient()
	{
		switch(CurrentGradientDrawMode)
		{
			case 0://left to right
			{
				gmGradient.Reset(clLeftclick,clRightclick,iGradSize,false);
				break;
			}
			case 1:
			{
				gmGradient.Reset(clRightclick,clLeftclick,iGradSize,false);
				break;
			}
			case 2:
			{
				gmGradient.Reset(clLeftclick,clRightclick,iGradSize,true);
				break;
			}
			case 3:
			{
				gmGradient.Reset(clRightclick,clLeftclick,iGradSize,true);
				break;
			}
			default:
		}
		btnGradient.repaint();
	}

	// Returns true if RGBA arrays are equivalent, false otherwise  
	// Could use Arrays.equals(int[], int[]), but this is probably a little faster...  
	private static boolean isEqualRgba(int[] pix1, int[] pix2) {  
		  //return pix1[0] == pix2[0] && pix1[1] == pix2[1] && pix1[2] == pix2[2] && pix1[3] == pix2[3];  
		  return pix1[0] == pix2[0] && pix1[1] == pix2[1] && pix1[2] == pix2[2];  
	}  
	private void QueueFloodfill(BufferedImage img, Point loc, Color clFill, boolean blUseGradient)
	{
		if (loc.x < 0 || loc.x >= img.getWidth() || loc.y < 0 || loc.y >= img.getHeight()) throw new IllegalArgumentException();  
		Point pt = new Point(loc.x,loc.y);

		GradientMaker Gradient = gmGradient.Copy();  
		Gradient.SetSteps(iGradSize);
		
		int[] fill =  new int[] {clFill.getRed(), clFill.getGreen(), clFill.getBlue(), clFill.getAlpha()}; 
		WritableRaster raster = img.getRaster();  
		int[] old = raster.getPixel(loc.x, loc.y, new int[4]);  
		int[] fillFake = new int[] {old[0]>127?old[0]-1:old[0]+1,old[1]>127?old[1]-1:old[1]+1,old[2]>127?old[2]-1:old[2]+1,255};
		LinkedList<Point> q = new LinkedList<Point>();
		int[] aux = {255, 255, 255, 255};  
		if(!isEqualRgba(raster.getPixel(loc.x, loc.y, aux),old))return;
		Rectangle bounds = raster.getBounds();
		//add to queue:
		q.add(pt);
		while (!q.isEmpty())
		{
			pt = q.pop();
			if(isEqualRgba(raster.getPixel(pt.x, pt.y, aux), old))
			{
				// finds the left side, filling along the way  
				int fillL = pt.x;  
				do {  
				    fillL--;  
				} while (fillL >= 0 && isEqualRgba(raster.getPixel(fillL, pt.y, aux), old));  
				fillL++;  
				    
				// find the right right side, filling along the way  
				int fillR = pt.x;  
				do {  
				  fillR++;  
				} while (fillR < bounds.width  && isEqualRgba(raster.getPixel(fillR, pt.y, aux), old));  
				//fillR--;
				//fill between those two:
				for(int t=fillL;t<fillR;t++)
				{
					if(blUseGradient)
					{
						fill=Gradient.GetIntArray(loc.x, loc.y, t, pt.y);
						if(isEqualRgba(fill,old))
							fill = fillFake;//avoid ever filling in target color
					}
					raster.setPixel(t, pt.y, fill);
				    
					// checks if applicable up or down  
					if (pt.y > 0 && isEqualRgba(raster.getPixel(t, pt.y - 1, aux), old)) 
					{
						Point ptNew = new Point(t, pt.y-1);
						q.push(ptNew);
					}  
					if (pt.y < bounds.height - 1 && isEqualRgba(raster.getPixel(t, pt.y + 1, aux), old)) 
					{
						Point ptNew = new Point(t, pt.y+1);
						q.push(ptNew);
					}  
				}
			}
		}
	}
	private void SelectTile()
	{
		//copy from the tilestrip to the working image
		imgThisTile = new BufferedImage(iTileSize,iTileSize,BufferedImage.TYPE_INT_RGB);
		Graphics g = imgThisTile.getGraphics();
		g.drawImage(imgTileSet,0,0,iTileSize,iTileSize,iTileSize*iTileIndex,0,iTileSize*(iTileIndex+1),iTileSize,null);
	}
	private void DrawTileOnStrip()
	{
		//copy from working image to tile strip
		Graphics g = imgTileSet.getGraphics();
		//g.drawImage(imgThisTile,iTileSize*iTileIndex,0,(iTileSize*(iTileIndex+1))-1,iTileSize-1,0,0,iTileSize-1,iTileSize-1,null);				
		g.drawImage(imgThisTile,iTileSize*iTileIndex,0,null);				
	}
	private void ResizeTilestrip()
	{
		Dimension dm = new Dimension(iTileSize, iTileSize);
		TileStrip.setPreferredSize(dm);
		pnlTop.setPreferredSize(new Dimension(iTileSize, iTileSize+32));
		pnlTop.revalidate();
		pnlTop.repaint();

	}
};
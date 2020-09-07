import javax.imageio.*; import java.io.*; import java.awt.*; import javax.swing.*;  import java.awt.event.*; import javax.swing.event.*; import java.awt.image.*; 
public class ColorPick
{
    Scale top = new Scale();
    Options bottom = new Options();
    Color[] colors = new Color[0];
    JFrame frame;
    int mode, spacing; 
    boolean deleteSense = false;
    boolean switchSense = false; 
    public static void main(String[]args)
    {
        ColorPick object = new ColorPick();
        object.run();
    }
    public void run()
    {   frame = new JFrame("Color Generator");
        frame.setSize(1000, 800);
        frame.setLocation(950, 0);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout());
        frame.setContentPane(content);
        frame.setVisible(true);
        
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 100)); 
        holder.setBackground(Color.WHITE);
        holder.setPreferredSize(new Dimension(1000, 500));
        holder.add(top); 
        content.add(holder, BorderLayout.NORTH);
        content.add(bottom, BorderLayout.SOUTH);
    }
    class Scale extends JPanel implements MouseListener, MouseMotionListener
    {
        int old = 100; 
        public Scale()
        {   
            setPreferredSize(new Dimension(800, 300));
            addMouseListener(this); 
        }
        public void paintComponent(Graphics g)
        {   super.paintComponent(g);
            double width = 800.0/colors.length; 
            g.setColor(Color.BLACK);
            for(int index = 0; index<colors.length; index++)
            {   g.setColor(colors[index]);
                g.fillRect((int)(index*width), 0, (int)width+1, 300); }
            if(mode==2) gradient(g);
            g.setColor(Color.BLACK);
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            
            if(switchSense && old!=100)
            g.drawRect((int)(old*width), 0, (int)(width), 300);    }
        public void gradient(Graphics g2)
        {   double width = 800.0/colors.length; 
            for(int index=1; index<colors.length; index++)
            {   double[]incre = { (colors[index-1].getRed()-colors[index].getRed())/width,
                               (colors[index-1].getGreen()-colors[index].getGreen())/width,
                               (colors[index-1].getBlue()-colors[index].getBlue())/width    };
                for(int index2 = 0; index2<width; index2++)
                {   g2.setColor(new Color((int)(colors[index-1].getRed()-incre[0]*index2), 
                                          (int)(colors[index-1].getGreen()-incre[1]*index2), 
                                          (int)(colors[index-1].getBlue()-incre[2]*index2) ));
                    g2.fillRect((int)(index*width-0.5*width)+index2, 0, 1, 300);
                }   }
        }
        public void mouseClicked(MouseEvent e)
        {   int x = e.getX();
            int y = e.getY();
            double width = 800.0/colors.length;
            
            if(deleteSense)
                bottom.removeColor((int)(x/width));
            if(switchSense)
            {   if(old>colors.length || (int)(x/width)==old ) old = (int)(x/width);
                else bottom.switchColors(old, (int)(x/width));    }
            repaint(); 
        }
        public void mousePressed(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseMoved(MouseEvent e){}
        public void mouseDragged(MouseEvent e){}
    }
    class Options extends JPanel implements ActionListener
    {
        JTextField type; 
        JToggleButton[] style = new JToggleButton[5];
        public Options()
        {   setLayout(new FlowLayout(FlowLayout.CENTER, 50, 100));
            setPreferredSize(new Dimension(1000, 300));
            setBackground(Color.BLACK);
            type = new JTextField(6);
            type.setPreferredSize(new Dimension(0, 50));
            type.setFont(new Font("Arial", Font.BOLD, 25));
            type.addActionListener(this);
            add(type);
            
            JPanel holder = new JPanel(new GridLayout(1, 2));
            holder.setPreferredSize(new Dimension(style.length*100, 50));
            ButtonGroup group = new ButtonGroup();
            String[]labels = {"BLOCKS", "GRADIENT", "REMOVE", "SWITCH", "SELECTOR"};
            for(int index=0; index<style.length; index++)
            {   style[index] = new JToggleButton(labels[index]);
                style[index].addActionListener(this);
                group.add(style[index]);
                holder.add(style[index]);   }
            style[0].setSelected(true);
            
            add(holder); 
            
            JButton save = new JButton("SAVE");
            save.addActionListener(this);
            save.setPreferredSize(new Dimension(100, 50)); 
            add(save); 
        }
        public void actionPerformed(ActionEvent e)
        {  String cmd = e.getActionCommand();
            if(e.getSource()==type && type.getText().length()==6)
            colorFinder(type.getText().toLowerCase());
            else if(cmd.equals("SAVE"))saveImage(); 
            else
            {
                if(cmd.equals("GRADIENT")) mode = 2; 
                else mode = 1; 
                if(cmd.equals("REMOVE")) deleteSense = true; 
                else deleteSense = false; 
                if(cmd.equals("SWITCH")) switchSense = true;
                else switchSense = false;
                if(cmd.equals("SELECTOR")) 
                {   JFrame temp = new JFrame("");
                    temp.setSize(600, 550);
                    temp.setLocation(frame.getX()+200, frame.getY()+50);
                    Selector content = new Selector();
                    temp.setContentPane(content);
                    temp.setVisible(true); }    
           }
                
            top.repaint(); 
        }
        public void colorFinder(String code)
        {   int red, blue, green;
            red=blue=green=256; 
            try
            {   red = Integer.parseInt(code.substring(0, 2), 16);
                green = Integer.parseInt(code.substring(2, 4), 16);
                blue = Integer.parseInt(code.substring(4, 6), 16);  }
            catch(NumberFormatException e){}
            if(Math.max(blue, (Math.max(red, green)))<=255)
            { 
                Color[]interm = colors; 
                colors = new Color[colors.length+1];
                for(int index=0; index<interm.length; index++)
                colors[index]=new Color(interm[index].getRed(),interm[index].getGreen(), interm[index].getBlue());
                colors[interm.length] = new Color(red, green, blue);    }
        }
        public void colorFinder(Color add)
        {   Color[]interm = colors; 
             colors = new Color[colors.length+1];
             for(int index=0; index<interm.length; index++)
             colors[index]=new Color(interm[index].getRed(),interm[index].getGreen(), interm[index].getBlue());
             colors[interm.length] = new Color(add.getRGB()); 
        }
        public void removeColor(int index)
        {   if(colors.length>0)
            {
                Color[]interm = new Color[colors.length];
                for(int num=0; num<colors.length; num++)
                interm[num] = new Color(colors[num].getRGB());
                
                colors=new Color[interm.length-1];
                for(int num=0; num<index; num++)
                colors[num]=new Color(interm[num].getRGB());
                
                for(int num=colors.length-1; num>=index; num--)
                colors[num]= new Color(interm[num+1].getRGB()); 
            }
        }
        public void switchColors(int num1, int num2)
        {   Color interm = new Color(colors[num1].getRGB()); 
            colors[num1]= new Color(colors[num2].getRGB());
            colors[num2] = new Color(interm.getRGB()); 
            top.old = 100; 
        }
        public BufferedImage getImage()
        {
            BufferedImage img = new BufferedImage(800, 300, BufferedImage.TYPE_INT_RGB);
            top.paint(img.getGraphics());
            return img; 
        }
        public void saveImage() 
        {
            BufferedImage img = getImage(); 
            try
            {ImageIO.write(img, "png", new File("Scale.png"));}
            catch(IOException e){}
        }
    }
    class Selector extends JPanel implements MouseListener, MouseMotionListener, ActionListener
    {
        Color picked, withDark; 
        int coorX, coorY, dX; 
        boolean touch = false; 
        JButton addit; 
        public Selector()
        {   setLayout(null); 
            addMouseListener(this);
            addMouseMotionListener(this);
            addit = new JButton("ADD COLOR");
            addit.addActionListener(this); 
            addit.setBounds(365, 20, 200, 110);
            add(addit); 
            picked = new Color(255, 255, 255); 
            withDark = new Color(255, 255, 255); 
            dX = 565; 
        }
        public void paintComponent(Graphics g)
        {   super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(25, 150, 540, 265);
            
            g.setColor(withDark);
            g.fillRect(25, 20, 200, 110);
            
            double inc = 255.0/90;
            for(int bris = 0; bris<255; bris++)
            {   for(int num = 0; num<=90; num++)
                {   g.setColor(new Color(255, (int)(num*inc)+(int)((255-num*inc)/255*bris), bris)); 
                    g.drawLine(num+25, 150+bris, num+25, 415);   }
                for(int num=0; num<=90; num++)
                {   g.setColor(new Color(255-(int)(num*inc)+(int)((num*inc)/255*bris),255, bris));
                    g.drawLine(num+115, 150+bris, num+115, 415); }
                for(int num=0; num<=90; num++)
                {   g.setColor(new Color(bris, 255, (int)(num*inc)+(int)((255-num*inc)/255*bris)));
                    g.drawLine(num+205, 150+bris, num+205, 415); }
                for(int num=0; num<=90; num++)
                {   g.setColor(new Color(bris, 255-(int)(num*inc)+(int)((num*inc)/255*bris), 255));
                    g.drawLine(num+295, 150+bris, num+295, 415); }
                for(int num=0; num<=90; num++)
                {   g.setColor(new Color((int)(num*inc)+(int)((255-num*inc)/255*bris), bris, 255));
                    g.drawLine(num+385, 150+bris, num+385, 415); }
                for(int num=0; num<=90; num++)
                {   g.setColor(new Color(255, bris, 255-(int)(num*inc)+(int)((num*inc)/255*bris)));
                    g.drawLine(num+475, 150+bris, num+475, 415); }  }
            g.setColor(picked);
            g.fillRect(25, 450, 540, 25);
            double[]incres = {picked.getRed()/540.0, picked.getGreen()/540.0, picked.getBlue()/540.0};
            for(int num=0; num<=540; num++)
            {   g.setColor(new Color((int)(incres[0]*num), (int)(incres[1]*num), (int)(incres[2]*num)));
                g.drawLine(num+25, 450, num+25, 475);   }
            if(touch)
            {   g.setColor(Color.BLACK); 
                g.drawOval(coorX-2, coorY-2, 4, 4);
                g.drawRect(dX-1, 450, 2, 25);   }
        }
        public void mouseClicked(MouseEvent e){}
        public void actionPerformed(ActionEvent e)
        {
            bottom.colorFinder(withDark); 
            top.repaint();
        }
        public void mousePressed(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            
            if(x>=25 && x<=565 && y>=150 && y<=415)
            {   coorX = x; coorY = y; 
                touch = true; 
                pickedColor(x-25, y-150);   }
            else if(x>=25 && x<=565 && y>=450 && y<475)
            {   dX = x; 
                pickedColor(x); }
            repaint(); 
        }
        public void pickedColor(int x, int y)
        {
            int div = x/90;
            double inc = 255.0/90; 
            int edit = 0; 
            if(div%2==0) edit = (int)(x%90*inc)+(int)((255-x%90*inc)/255*y); 
            else 
            edit = 255-(int)(x%90*inc)+(int)((x%90*inc)/255*y); 
            
            try
            {   if(div==0) picked = new Color(255, edit, y);
                else if(div==1) picked = new Color(edit, 255, y);
                else if(div==2) picked = new Color(y, 255, edit);
                else if(div==3) picked = new Color(y, edit, 255);
                else if(div==4) picked = new Color(edit, y, 255);
                else if(div==5) picked = new Color(255, y, edit);   }
            catch(IllegalArgumentException e)
            { picked = new Color(255, 255, 255); }
            pickedColor(dX); 
        }
        public void pickedColor(int dark)
        {
            dark-=25; 
            double[] incres = {picked.getRed()/540.0, picked.getGreen()/540.0, picked.getBlue()/540.0};
            withDark = new Color((int)(incres[0]*dark), (int)(incres[1]*dark), (int)(incres[2]*dark));
        }
        public void mouseReleased(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        public void mouseMoved(MouseEvent e){}
        public void mouseDragged(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            
            if(x>=25 && x<=565 && y>=150 && y<=415)
            {   coorX = x; coorY=y; 
                touch = true;
                pickedColor(x-25, y-150);   }
            else if(x>=25 && x<=565 && y>=450 && y<475)
            {   dX = x; 
                pickedColor(x); }
            repaint(); 
        }
    }
}
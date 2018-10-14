/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package investmentpso;

import java.awt.Color;
import java.util.ArrayList;
import javafx.scene.chart.ValueAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import particlegraph.MainJFrame;

/**
 *
 * @author kunal
 */
public class ParticleThread implements Runnable{

    @Override
    public void run() {
        
        // Get object from InvestmentPSO class
        ArrayList<Particle> particles = InvestmentPSO.particles;
        MainJFrame mj = InvestmentPSO.mj;
        int gBest = InvestmentPSO.globalBest;
        Particle gBestParticle = particles.get(gBest);
               
        // Create dataset                    
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series1 = new XYSeries("Particles");
        XYSeries series2 = new XYSeries("Global Best");
        
    
        for(Particle p : particles){
            if(p == gBestParticle){
                series2.add(p.data(0), p.data(1));

                
            }else{
            series1.add(p.data(0), p.data(1));
            }
        }

        dataset.addSeries(series1);
        dataset.addSeries(series2);
        XYDataset xydataset = dataset;
                        
                        
                            
        // Create chart
        JFreeChart chart = ChartFactory.createScatterPlot(
        "PPF vs FD for Epoch "+InvestmentPSO.epoch, 
        "PPF Investment", "FD Investment", xydataset);
        
        

    
        //Changes background color
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(new Color(196,164,196));
        org.jfree.chart.axis.ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setAutoRange(false);
        domainAxis.setRange(0.0, 160000.0);

        org.jfree.chart.axis.ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(0.0, 160000.0);           
   
        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        mj.getContentPane().removeAll();
        mj.setContentPane(panel);                        
                        
        mj.setVisible(true);        
        
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package investmentpso;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kunal
 */
public class FDInvestment {
    
    private static Map<Integer, Double> map;
    
    private int amount;
    // this is the roi calculated using (tenure - current year) because we are assuming rate we get if this FD is not prematurely broken
    private double roi;
    private int year;
    
    static {
        map = new HashMap<>();
        map.put(0, 0.055);
        map.put(1, 0.055);
        map.put(2, 0.055);        
        map.put(3, 0.060);
        map.put(4, 0.060);
        map.put(5, 0.060);        
        map.put(6, 0.065);
        map.put(7, 0.065);
        map.put(8, 0.065);        
        map.put(9, 0.0675);
        map.put(10,0.0675);
        map.put(11,0.0675);        
        map.put(12, 0.0690); 
        map.put(13, 0.0690); 
        map.put(14, 0.0690); 
        map.put(15, 0.0690);         
        
    }

    public FDInvestment(int amount, double roi, int year) {
        this.amount = amount;
        this.roi = roi;
        this.year = year;
    }

    public FDInvestment() {
    }
    
    
    

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getRoi() {
        return roi;
    }

    public void setRoi(double roi) {
        this.roi = roi;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
    public static double getRateByTerm(double t){
        int i = (int) Math.floor(t);
        
        return map.get(new Integer(i));
    }
    
    public double calculatePenalty(int currentYear){
        int t = currentYear - this.year + 1;
        if(t == 0) t=1;
        
        double p = this.getAmount();
        // penalty of 1%
        double r = 0.01;
        // compounded quarterly
        int n = 4;
        double d;
        double exp1 = t*n;
        double exp2 = 1.0 + ((double)(r))/(n);
        d = Math.pow(exp2, exp1);
        d = p * d;
        d = d - p;
        return d;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package investmentpso;

/**
 *
 * @author kunal
 */
public class PPFInvestment {
    
    private int amount;
    private double roi;
    private int year;

    public PPFInvestment(int amount, double roi, int year) {
        this.amount = amount;
        this.roi = roi;
        this.year = year;
    }

    public PPFInvestment() {
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
    
    
    
}

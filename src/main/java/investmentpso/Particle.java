/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package investmentpso;

import java.util.ArrayList;

/**
 *
 * @author kunal
 */
public class Particle 
{
        // current values
        private int mData[] ;
        private double interestPPF;
        private double interestFD;
        private double withdrawalPPF;
        private double withdrawalFD;
        private double penaltyFD;
        
        // personal best values
        private double mpBestIntPPF ;
        private double mpBestIntFD;
        private double mpBestWithPPF ;
        private double mpBestWithFD;
        private double mpBestPenFD;
        private int mpBestData[] ;
        
        // velocity
        private double[] mVelocity;
    
        public Particle()
        {
            this.mVelocity = new double[InvestmentPSO.MAX_BREAKUP_VALUES];
            mpBestData = new int[InvestmentPSO.MAX_BREAKUP_VALUES];
            mData = new int[InvestmentPSO.MAX_BREAKUP_VALUES];
        }
    
        public int data(int index)
        {
            return this.mData[index];
        }
        
        public void data(int index, int value)
        {
            this.mData[index] = value;
            return;
        }

    public double getInterestPPF() {
        return interestPPF;
    }

    public void setInterestPPF(double interestPPF) {
        this.interestPPF = interestPPF;
    }

    public double getInterestFD() {
        return interestFD;
    }

    public void setInterestFD(double interestFD) {
        this.interestFD = interestFD;
    }

    public double getWithdrawalPPF() {
        return withdrawalPPF;
    }

    public void setWithdrawalPPF(double withdrawalPPF) {
        this.withdrawalPPF = withdrawalPPF;
    }

    public double getWithdrawalFD() {
        return withdrawalFD;
    }

    public void setWithdrawalFD(double withdrawalFD) {
        this.withdrawalFD = withdrawalFD;
    }

    public double getPenaltyFD() {
        return penaltyFD;
    }

    public void setPenaltyFD(double penaltyFD) {
        this.penaltyFD = penaltyFD;
    }

    public double getMpBestIntPPF() {
        return mpBestIntPPF;
    }

    public void setMpBestIntPPF(double mpBestIntPPF) {
        this.mpBestIntPPF = mpBestIntPPF;
    }

    public double getMpBestIntFD() {
        return mpBestIntFD;
    }

    public void setMpBestIntFD(double mpBestIntFD) {
        this.mpBestIntFD = mpBestIntFD;
    }

    public double getMpBestWithPPF() {
        return mpBestWithPPF;
    }

    public void setMpBestWithPPF(double mpBestWithPPF) {
        this.mpBestWithPPF = mpBestWithPPF;
    }

    public double getMpBestWithFD() {
        return mpBestWithFD;
    }

    public void setMpBestWithFD(double mpBestWithFD) {
        this.mpBestWithFD = mpBestWithFD;
    }

    public double getMpBestPenFD() {
        return mpBestPenFD;
    }

    public void setMpBestPenFD(double mpBestPenFD) {
        this.mpBestPenFD = mpBestPenFD;
    }
    
        
    
        public double velocity(int index)
        {
            
            return this.mVelocity[index];
        }
        
        public void velocity(double velocityScore, int index)
        {
           this.mVelocity[index] = velocityScore;
           return;
        }
        
        public void makeDataEqualPBest(){
            
            for(int i = 0; i < InvestmentPSO.MAX_BREAKUP_VALUES; i++){
                mData[i] = mpBestData[i];
            }
            
            this.interestFD = this.mpBestIntFD;
            this.interestPPF = this.mpBestIntPPF;
            this.withdrawalFD = this.mpBestWithFD;
            this.withdrawalPPF = this.mpBestWithPPF;
            this.penaltyFD = this.mpBestPenFD;            

        }
        
        public void makePBestEqualData(){
            
            for(int i = 0; i < InvestmentPSO.MAX_BREAKUP_VALUES; i++){
                mpBestData[i] = mData[i];
            }
            this.mpBestIntFD = this.interestFD;
            this.mpBestIntPPF = this.interestPPF;
            this.mpBestWithFD = this.withdrawalFD;
            this.mpBestWithPPF = this.withdrawalPPF;
            this.mpBestPenFD = this.penaltyFD;

        }

    public void updateValuesFromCurrentData() {
        
        // interest from PPF investment compounded monthly, for 1 year
        this.interestPPF = getCompoundInterestPPF(this.data(0), InvestmentPSO.roi, 1, 12);
        
        // interest from FD investment compounded quarterly, for 1 years time
        this.interestFD = getCompoundInterestFD(this.data(1), 1, 4);
        
        // How much can i withdraw from PPF
        this.withdrawalPPF = calculateWithdrawPPF(InvestmentPSO.ppfInvestments, this.data(0) ,InvestmentPSO.roi, InvestmentPSO.currentYear);
        
        // How much can i withdraw from FD // Penalty from FD withdrawal
        updateWithdrawPenaltyFD(InvestmentPSO.fdInvestments, this.data(1), InvestmentPSO.currentYear);

        
    }

    private double getCompoundInterestPPF(double p, double r, int t, int n) {
        
        double d;
        double exp1 = t*n;
        double exp2 = 1.0 + ((double)(r))/(n);
        d = Math.pow(exp2, exp1);
        d = p * d;
        d = d - p;
        
        return d;
    }
    
    private double getCompoundInterestFD(double p, int t, int n) {
        
        double r = FDInvestment.getRateByTerm(t);
        double d;
        double exp1 = t*n;
        double exp2 = 1.0 + ((double)(r))/(n);
        d = Math.pow(exp2, exp1);
        d = p * d;
        d = d - p;
        
        // interest is taxable (Tax assumed at 10%)
        d = 0.9 * d;
        
        
        return d;
    }    
    
    public double calculateWithdrawPPF(ArrayList<PPFInvestment> ppfInvestments, int amt, double r, int year){
        
        if(year <= 7) return 0;
        
        // calculate balance at end of (current-4)th year
        int y = year-4;
        int count = 0;
        double total1 = ppfInvestments.get(0).getAmount();
        for(PPFInvestment investment : ppfInvestments){
            total1 += getCompoundInterestPPF(total1, investment.getRoi(), 1, 12);
            count++;
            if(count == y){
                break;
            }
        }
        
        // calculate balance at the end of previous year
        y = year-1;
        count = 0;
        double total2 = ppfInvestments.get(0).getAmount();
        for(PPFInvestment investment : ppfInvestments){
            total2 += getCompoundInterestPPF(total2, investment.getRoi(), 1, 12);
            count++;
            if(count == y){
                break;
            }
        }        
        
        
        // return 50% of whichever is lower
        if(total1 < total2)
            return (total1/2);
        else
            return (total2/2);
    }

    public void updateWithdrawPenaltyFD(ArrayList<FDInvestment> fdInvestments, int amt, int currentYear) {
        
        int amt1 = amt;
        int amt2= 0;
        int amt3 = 0;
        int k,j=0;
        double pen1 = 0, pen2 = 0, pen3 = 0;
        boolean found = false;
        
        FDInvestment fdInvestment = new FDInvestment();
        fdInvestment.setAmount(amt);
        fdInvestment.setRoi(FDInvestment.getRateByTerm(InvestmentPSO.tenure-currentYear));
        fdInvestment.setYear(currentYear);
        
        pen1 = fdInvestment.calculatePenalty(currentYear);
        
        for(k = fdInvestments.size()-1; k >=0; k--){
            amt2 = fdInvestments.get(k).getAmount();
            pen2 = fdInvestments.get(k).calculatePenalty(currentYear);            
            amt3 = 0; pen3 = 0;
            
               if((amt1+amt2)>= (InvestmentPSO.withdrawalAmt - this.withdrawalPPF)){
                   if((pen1+pen2)<=InvestmentPSO.penalty){
                       found = true;
                       break;
                   }
               }
            
            
            for(j = k-1; j>=0; j--){
               amt3 = fdInvestments.get(j).getAmount();
                
               if((amt1+amt2+amt3)>= (InvestmentPSO.withdrawalAmt - this.withdrawalPPF)){

                   pen3 = fdInvestments.get(j).calculatePenalty(currentYear);
                   if((pen1+pen2+pen3)<=InvestmentPSO.penalty){
                       found = true;
                       break;
                   }
               }
               
            }
            if(found)break;
    
        }
        this.withdrawalFD = amt1+amt2+amt3;
        this.penaltyFD = pen1+pen2+pen3;
        
        
    }
    
    }


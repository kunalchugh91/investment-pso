/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package investmentpso;

import static investmentpso.InvestmentPSO.V_MAX;
import static investmentpso.InvestmentPSO.particles;
import static investmentpso.InvestmentPSO.penalty;
import static investmentpso.InvestmentPSO.withdrawalAmt;
import java.util.Random;
import static investmentpso.InvestmentPSO.MAX_NO_PARTICLES;

/**
 *
 * @author kunal
 */
public class ConcurrentThread implements Runnable{

    private int c;
    private int gBestindex;
    
    public ConcurrentThread() {
    }

    public ConcurrentThread(int c, int gBestindex) {
        this.c = c;
        this.gBestindex = gBestindex;
    }
    

    @Override
    public void run() {
        
        System.out.println("Inside ConcurrentThread "+ c);
        
        double vValue;
        Particle aParticle = InvestmentPSO.particles.get(c);
        Particle gBestParticle = particles.get(gBestindex);

        
        
            // update velocity for PPF interest
            //aParticle = particles.get(i);
            vValue = aParticle.velocity(0) + 
                     2 * new Random().nextDouble() * (aParticle.getMpBestIntPPF() - aParticle.getInterestPPF()) + 
                     2 * new Random().nextDouble() * (gBestParticle.getInterestPPF() - aParticle.getInterestPPF());

            if(vValue > V_MAX){
                aParticle.velocity(V_MAX, 0);
            }else if(vValue < -V_MAX){
                aParticle.velocity(-V_MAX, 0);
            }else{
                aParticle.velocity(vValue, 0);
            }
            
            // update velocity for FD interest

            vValue = aParticle.velocity(1) + 
                     2 * new Random().nextDouble() * (aParticle.getMpBestIntFD() - aParticle.getInterestFD()) + 
                     2 * new Random().nextDouble() * (gBestParticle.getInterestFD() - aParticle.getInterestFD());

            if(vValue > V_MAX){
                aParticle.velocity(V_MAX, 1);
            }else if(vValue < -V_MAX){
                aParticle.velocity(-V_MAX, 1);
            }else{
                aParticle.velocity(vValue, 1);
            }            
            
            
            
            // update this particle
            
        Particle gBParticle = particles.get(gBestindex);

        
        
            
            
                if(particles.get(c).data(0) != gBParticle.data(0)){
                    particles.get(c).data(0, particles.get(c).data(0) + (int)Math.round(particles.get(c).velocity(0)));
                    particles.get(c).data(1, InvestmentPSO.investmentAmt-particles.get(c).data(0) );
                }
            
                particles.get(c).updateValuesFromCurrentData();
            
            /*    
            // Check pBest value.
            int totalInt = testProblemInt(i, true);
            int totalPen = testProblemPen(i, true);
            
            if((Math.abs(TARGET - totalInt) < Math.abs(TARGET - particles.get(i).pBestInt()) ) && totalPen <= TARGETPEN){
                particles.get(i).pBestInt(totalInt);
                particles.get(i).pBestPen(totalPen);
                particles.get(i).makePBestEqualData();
            }
            */
            Particle particle = particles.get(c);
            double totalWith = particle.getWithdrawalPPF() + particle.getWithdrawalFD();
            double totalWithPBest = particle.getMpBestWithFD() + particle.getMpBestWithPPF();
            double penFD = particle.getPenaltyFD();
            double penFDBest = particle.getMpBestPenFD();
            
            // if both current and pBest saisfy withdrawal and penalty condition
            if((totalWith >= withdrawalAmt && penFD <= penalty) && 
               (totalWithPBest >= withdrawalAmt && penFDBest <= penalty)){
                
                //check whose interest is better
                if((particle.getInterestPPF()+particle.getInterestFD())>(particle.getMpBestIntPPF()+particle.getMpBestIntFD())){
                    // update the PBest to the current data values
                    particle.makePBestEqualData();
                }
                
            }
            
            // if only current and not PBest satisfies the withdrawal and penalty condition
            if((totalWith >= withdrawalAmt && penFD <= penalty) && 
                !(totalWithPBest >= withdrawalAmt && penFDBest <= penalty)){
                
                // update the PBest to the current data values
                particle.makePBestEqualData();
                
            }            
            
            // if only PBest and not current satisfy the withdrawal and penalty condition, let PBest remain as is
            
            // if both dont satisfy the withdrawal and penalty condition, update the PBest to the new data
            if(!(totalWith >= withdrawalAmt && penFD <= penalty) && 
                !(totalWithPBest >= withdrawalAmt && penFDBest <= penalty)){
                
                particle.makePBestEqualData();
                
            } 

                   
             
        
    }
    
}

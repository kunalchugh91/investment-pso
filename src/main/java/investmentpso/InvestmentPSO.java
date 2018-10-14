/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package investmentpso;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import particlegraph.MainJFrame;

/**
 *
 * @author kunal
 */
public class InvestmentPSO {
    
    public static int tenure = 15; // 15 years
    public static int currentYear;
    public static double roi;
    public static int withdrawalAmt;
    public static int penalty;
    public static int showGraph = 0;
    
    public static ArrayList<PPFInvestment> ppfInvestments = new ArrayList<PPFInvestment>();
    public static ArrayList<FDInvestment> fdInvestments = new ArrayList<FDInvestment>();
    
    public static int investmentAmt = 150000; //investment amount per year
    public static int MAX_EPOCHS = 200;
    public static int MAX_NO_PARTICLES = 20;
    public static int MAX_BREAKUP_VALUES = 2 ;
    public static int TARGET = 250000;
    public static int FD_STARTMIN = 100000;
    public static int FD_STARTMAX = 140000;
    public static int V_MAX = 1000;
    public static ArrayList<Particle> particles = new ArrayList<>();
    
    public static MainJFrame mj ;
    public static int epoch = 0;
    public static int globalBest;
    public static boolean multithreading = false;

    static {
        mj = new MainJFrame("Particle Swarm Optimization");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // TODO code application logic here
        Scanner scan = new Scanner(System.in);
        
      for( currentYear = 1; currentYear <= tenure; currentYear++ ){ 
          
          System.out.println("Current Year "+ currentYear+". Press 1 to continue");
          if(scan.nextInt() == 1)
          {
          System.out.println("Enter the rate of interest for the year "+ currentYear);
          roi = scan.nextDouble();
          roi = roi / 100;
          
          System.out.println("Enter the max amount you wish to be able withdraw incase of financial emergency");
          withdrawalAmt = scan.nextInt();
          
          System.out.println("Enter the max amount of penalty you can pay");
          penalty = scan.nextInt();
          
          System.out.println("Show PSO iterations on graph. \n 1 -> Yes \n 0 -> No, only console");
          showGraph = scan.nextInt();
          
          System.out.println("Multithreading enabled ? \n 1. Yes \n 0. No");
          if(scan.nextInt() == 1) {multithreading = true;}
          else{ multithreading = false;}
          
     
          System.gc();
          initialize();
          PSO();
          
        
          }
    }
      // display all the PPF and Investment values found
      int size = ppfInvestments.size();
      for(int i = 0; i < size; i++){
          PPFInvestment ppfInvestment = ppfInvestments.get(i);
          FDInvestment fdInvestment = fdInvestments.get(i);
          System.out.println("Year "+ ppfInvestment.getYear());
          System.out.println("PPF: "+ppfInvestment.getAmount()+" FD: "+fdInvestment.getAmount());
      }
      
      
    }
    
    public static void initialize(){
        particles = new ArrayList<Particle>();
        for(int i = 0; i<MAX_NO_PARTICLES; i++){
            Particle p = new Particle();
            p.data(1, new Random().nextInt(FD_STARTMAX-FD_STARTMIN) + FD_STARTMIN);
            p.data(0, (investmentAmt-p.data(1)));
            p.updateValuesFromCurrentData();
            p.makePBestEqualData();
            particles.add(p);
        }
    }
    
    
    public static void PSO() throws InterruptedException, ExecutionException{
        
        int gBest = 0;
        int gBestTest = 0;
        int gBestTestP = 0;
        Particle aParticle = null;
        epoch = 0;
        boolean done = false;

        while(!done)
        {
            //To endingTime loop
            //    if the maximum number of epochs allowed has been reached, or,
            //    if the Target value has been found.
            if(epoch < MAX_EPOCHS){
                
                
                for(int i = 0; i < MAX_NO_PARTICLES; i++)
                {
                    aParticle = particles.get(i);
                    for(int j = 0; j < MAX_BREAKUP_VALUES; j++)
                    {
                        if(j < MAX_BREAKUP_VALUES - 1){
                            System.out.print(aParticle.data(j) + " + ");
                        }else{
                            System.out.print(aParticle.data(j)+ " = ");
                        }
                    } 

                    System.out.print(aParticle.getInterestPPF()+ " " 
                                     + aParticle.getInterestFD()+ " " 
                                     + aParticle.getWithdrawalPPF()+ " "
                                     + aParticle.getWithdrawalFD()+ " "
                                     + aParticle.getPenaltyFD()+"\n");
                    
                    
             
                } 

                gBestTest = minimum();
                //gBestTestP = minimumPBest();
                Particle pGBest = particles.get(gBest);
                Particle pGBestTest = particles.get(gBestTest);
                // if(any particle's pBest value is better than the gBest value, make it the new gBest value.
                /*
                if(Math.abs(TARGET - testProblemInt(gBestTest, true)) < Math.abs(TARGET - testProblemInt(gBest, true))){
                    gBest = gBestTest;
                }*/
                
                    // Set the new GBest if that is the case
                    double totalWithdrawalAmt = pGBestTest.getWithdrawalFD()+pGBestTest.getWithdrawalPPF();
                    if((totalWithdrawalAmt) >= withdrawalAmt){
                        
                        if(pGBestTest.getPenaltyFD() <= penalty){
                            
                            double totalInt = pGBestTest.getInterestFD() + pGBestTest.getInterestPPF();
                            double totalIntGBest = pGBest.getInterestFD() + pGBest.getInterestPPF();
                            
                            if(totalInt > totalIntGBest){
                                gBest = gBestTest;
                                
                            }
                        }
                    
                    }                
                System.out.println("GBEST "+gBest);
                globalBest = gBest;

                // display current particle values on JFreeChart
                if(showGraph == 1)
                displayParticlesJFreeChart();
                
                if(!multithreading){
                   // update the velocity of particles based on GBest particle 
                   updateVelocity(gBest);

                   // update the particle values based on velocity
                   updateParticles(gBest);
                }
                else{
                    // call ConcurrentThread 
                  int poolSize = 10;
                  ExecutorService s = Executors.newFixedThreadPool(poolSize);
                  List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

                  for (int i = 0; i < MAX_NO_PARTICLES; i++)
                  {
                     Future f = s.submit(new ConcurrentThread(i, globalBest));
                     futures.add(f);
                  }

                   // All ConcurrentThreads should complete before going ahead
                   for (Future<Runnable> f : futures)
                   {
                    f.get();
                   }

                   // Shut down
                   s.shutdownNow();                    
                    
                    
                }
                
                System.out.println("epoch number: " + epoch);

                epoch += 1;

            }else{
                done = true;
            }
        }
        
        Particle gBestParticle = particles.get(gBest);
        PPFInvestment ppfInvestment = new PPFInvestment(gBestParticle.data(0), InvestmentPSO.roi, InvestmentPSO.currentYear);
        ppfInvestments.add(ppfInvestment);
        FDInvestment fdInvestment = new FDInvestment(gBestParticle.data(1), 
                                                     FDInvestment.getRateByTerm(tenure-currentYear),
                                                     InvestmentPSO.currentYear);
        fdInvestments.add(fdInvestment);
        
        System.out.println("After PSO");
        System.out.println("PPF: "+ppfInvestment.getAmount()+" FD: "+fdInvestment.getAmount());
    }
    
    private static int minimum()
    {
    // Returns an array index.
        int winner = 0;
        boolean NewWinnerFound = false;
        boolean done = false;
        
        // set initial winner value
        for(int i = 0; i < MAX_NO_PARTICLES; i++){
            Particle p = particles.get(i);
            double totalWithAmt = p.getWithdrawalFD()+p.getWithdrawalPPF();
            if(totalWithAmt >= withdrawalAmt && p.getPenaltyFD() <= penalty){
                winner = i;
                System.out.println("Found initial winner " + i);
                break;
                
            }
        }

        while(!done)
        {
            NewWinnerFound = false;
            for(int i = 0; i < MAX_NO_PARTICLES; i++)
            {
                if(i != winner){             // Avoid self-comparison.
                    
                    Particle particle = particles.get(i);
                    Particle winParticle = particles.get(winner);
                    double totalWithdrawalAmt = particle.getWithdrawalFD()+particle.getWithdrawalPPF();
                    if((totalWithdrawalAmt) >= withdrawalAmt){
                        
                        if(particle.getPenaltyFD() <= penalty){
                            
                            double totalInt = particle.getInterestFD() + particle.getInterestPPF();
                            double totalIntWinner = winParticle.getInterestFD() + winParticle.getInterestPPF();
                            
                            if(totalInt > totalIntWinner){
                                winner = i;
                                NewWinnerFound = true;
                            }
                        }
                    
                    }
                    
                }
            }

            if(NewWinnerFound == false){
                done = true;
            }
        }

        return winner;
    }
    
    private static int testProblemInt(int index, boolean b)
    {
        if(b){
        // total interest for this particle based on mData    
        int total = 0;
        Particle aParticle = null;

        aParticle = particles.get(index);

        for(int i = 0; i < MAX_BREAKUP_VALUES; i++)
        {
            total += aParticle.data(i);
        }
        return total;
        }
        else {
        // total interest for this particle based on mpBestData
        int total = 0;
        Particle aParticle = null;

        aParticle = particles.get(index);

        for(int i = 0; i < MAX_BREAKUP_VALUES; i++)
        {
            total += aParticle.data(i);
        }
        return total;        
            
        }
    }
    
    private static int testProblemPen(int index, boolean b)
    {
        if(b){
        // total penalty for this particle based on mData    
        int total = 0;
        Particle aParticle = null;

        aParticle = particles.get(index);

        for(int i = 0; i < MAX_BREAKUP_VALUES; i++)
        {
            total += aParticle.data(i);
        }
        return total;
        }
        else {
        // total penalty for this particle based on mpBestData
        int total = 0;
        Particle aParticle = null;

        aParticle = particles.get(index);

        for(int i = 0; i < MAX_BREAKUP_VALUES; i++)
        {
            total += aParticle.data(i);
        }
        return total;        
            
        }
    }    
    
    /*
    private static int minimumPBest()
    {
    // Returns an array index.
        int winner = 0;
        boolean NewWinnerFound = false;
        boolean done = false;

        while(!done)
        {
            NewWinnerFound = false;
            for(int i = 0; i < MAX_NO_PARTICLES; i++)
            {
                if(i != winner){             // Avoid self-comparison.
                    
                    if(testProblemPen(i, false) <= TARGETPEN){
                    // The minimum has to be in relation to the Target.
                    if(Math.abs(TARGET - testProblemInt(i, false)) < Math.abs(TARGET - testProblemInt(winner, false))){
                        winner = i;
                        NewWinnerFound = true;
                    }}
                }
            }

            if(NewWinnerFound == false){
                done = true;
            }
        }

        return winner;
    }    
    */

    private static void updateVelocity(int gBestindex)
{
    //  from Kennedy & Eberhart(1995).
    //    vx[][] = vx[][] + 2 * rand() * (pbestx[][] - presentx[][]) + 
    //                      2 * rand() * (pbestx[][gbest] - presentx[][])

        double vValue;
        Particle aParticle;
        Particle gBestParticle = particles.get(gBestindex);

        for(int i = 0; i < MAX_NO_PARTICLES; i++)
        {
            // update velocity for PPF interest
            aParticle = particles.get(i);
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
            
            
        }
    }
            

        private static void updateParticles(int gBestindex)
    {
        Particle gBParticle = particles.get(gBestindex);

        for(int i = 0; i < MAX_NO_PARTICLES; i++)
        {
            
            
                if(particles.get(i).data(0) != gBParticle.data(0)){
                    particles.get(i).data(0, particles.get(i).data(0) + (int)Math.round(particles.get(i).velocity(0)));
                    particles.get(i).data(1, InvestmentPSO.investmentAmt-particles.get(i).data(0) );
                }
            
                particles.get(i).updateValuesFromCurrentData();
            
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
            Particle particle = particles.get(i);
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

        } // i
        
    }

    public static void displayParticlesJFreeChart() {
        
        try {
            SwingUtilities.invokeAndWait(
                    
                    new ParticleThread()
                    
            );        
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Logger.getLogger(InvestmentPSO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            Logger.getLogger(InvestmentPSO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long startingTime = System.currentTimeMillis();
        
        long endingTime = 0;
        
        while(true){
            
            endingTime = System.currentTimeMillis();
            
            if((endingTime-startingTime)>300)
            break;
        }
        
    }

    
    
        
        
    }

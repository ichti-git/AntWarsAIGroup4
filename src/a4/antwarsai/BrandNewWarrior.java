package antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ichti (Simon T)
 */
public class BrandNewWarrior extends SharedAI implements IAntAI {
    private boolean runaway = false;
    private final Random rnd = new Random();
    private Random gen = new Random();
    int[] arrayinfo = new int[2];
    int[] arrayLeftOrRightMove = new int[1];
    private ArrayList<EAction>  listofActions = new ArrayList<EAction>();
    
    @Override
    public EAction chooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        
        sharedChooseAction(thisAnt,  thisLocation, visibleLocations, possibleActions);//Simon method
        
        
        EAction action = null;
        
         if (arrayinfo[1] == 1) {
            System.out.println(" I AM IN SITUATION WHERE I MET BLOCKING CUBE I TURNED AND I HAVE BLOCKING CUBE AGAIN ");
//            if (possibleActions.contains(EAction.TurnRight)) {
//                System.out.println(" I AM TURINING RIGHT ");
                if(listofActions.get(0)==EAction.TurnLeft)
                {
                arrayinfo[0] = 0;
                arrayinfo[1] = 0;
                listofActions.clear();
                
                return  EAction.TurnLeft; 
                
                }else
                {
                    
                      arrayinfo[0] = 0;
                arrayinfo[1] = 0;
                listofActions.clear();
                
               
                    return EAction.TurnRight; 
                }
//                return EAction.TurnRight;
         }
         else if(thisAnt.getFoodLoad()<=3 )
        {
            if(possibleActions.contains(EAction.PickUpFood))
            {
                return EAction.PickUpFood;
            }
        }
        
        else if (thisAnt.getHitPoints() < 15) 
        {
            
           // if(thisAnt.getFoodLoad()==0 )
           // {
               // if(possibleActions.contains(EAction.PickUpFood))
              //  {
                //    System.out.println("I AM PICKING UP FOOD ");
                //    return EAction.PickUpFood;
             //   }
          //  }else
          //  {
                
               // System.out.println(" I EAT FOOD ");
                if(possibleActions.contains(EAction.EatFood))
               {
            return EAction.EatFood;
                }else
                {
                   return EAction.Pass; 
                }
        } else if (possibleActions.contains(EAction.Attack) && visibleLocations.get(0).getAnt().getTeamInfo().getTeamID() != thisAnt.getTeamInfo().getTeamID()) {
            return EAction.Attack;
            
            
            
        } 
//else if(runaway)
//                    {
//                       if(possibleActions.contains(EAction.MoveBackward))
//                       {
//                           return EAction.MoveBackward;
//                       }
//                       else if(possibleActions.contains(EAction.TurnLeft))
//                       {
//                           return EAction.TurnLeft;
//                       }else if(possibleActions.contains(EAction.TurnRight))
//                       {
//                           return EAction.TurnRight;
//                       }
//                    }
        else if (possibleActions.contains(EAction.MoveForward)) {
            //empty arrays
            return EAction.MoveForward;
        } else if (arrayLeftOrRightMove[0] != 0)//ant moved back
        {
            if (arrayLeftOrRightMove[0] == 1)//it is 1
            {
                if (possibleActions.contains(EAction.TurnLeft)) {
                    
                    arrayLeftOrRightMove[0] = 0;
                    return EAction.TurnLeft;
                    
                } else if (possibleActions.contains(EAction.TurnRight)) {
                    {
                        arrayLeftOrRightMove[0] = 0;
                        return EAction.TurnRight;
                    }
                } else if (possibleActions.contains(EAction.Pass)) {
                    arrayLeftOrRightMove[0] = 0;
                    return EAction.Pass;
                }
            } else if (arrayLeftOrRightMove[0] == 2)//or 2
            {
                if (possibleActions.contains(EAction.TurnRight)) {
                    
                    arrayLeftOrRightMove[0] = 0;
                    return EAction.TurnRight;
                    
                } else if (possibleActions.contains(EAction.TurnLeft)) {
                    {
                        arrayLeftOrRightMove[0] = 0;
                        return EAction.TurnLeft;
                    }
                } else if (possibleActions.contains(EAction.Pass)) {
                    return EAction.Pass;
                }
            }
//*********************************************************NEEDS REDO********************************************
        } else if (!visibleLocations.isEmpty()) {
            if (!visibleLocations.get(0).isFilled() /*|| /*!visibleLocations.get(0).isRock()*/) {
                if (possibleActions.contains(EAction.Pass)) {
                    return EAction.Pass;//not enough action points to move forward
                }
            } else {//IS NOT EMPTY IN FRONT OF ME 
                int rightorleft = gen.nextInt(100) + 1;
                System.out.println("ENCOUTERED A ROCK ");
                System.out.println("THE VALUE OF rightorleft is "+rightorleft);
                //{
                    
                if (rightorleft > 50) {
                    
                    if (possibleActions.contains(EAction.TurnLeft)) {
                        
                        //REMEMBER THE LAST MOVE INTO A LIST
                        listofActions.add(EAction.TurnLeft);//not necessary
                        //ADDED TURN LEFT TO THE ARRAYLIST
                        
                        
                        if (arrayinfo[0] == 1) {
                            arrayinfo[1] = 1;
                            //here i should empty the array
                           // arrayinfo[0]=0;
                            //arrayinfo[1]=0;
                            //listofActions.clear();
                            
                            
                        } else {
                            arrayinfo[0] = 1;//there is no ant in front of me, there is something blocking
                        }
                        
                        //method for returning the action
                        
                        
                        System.out.println(" I AM TURNING LEFT AFTER ENCOUNTER A ROCK");
                        return EAction.TurnLeft;
                    } 
//                        else if (possibleActions.contains(EAction.TurnRight)) {
//                        
//                        System.out.println(" I ENCOUNTERED A ROCK NOT POSSIBLE TURN LEFT SO I TURN RIGHT ");
//                        return EAction.TurnRight;
//                    }
                } else if (possibleActions.contains(EAction.TurnRight)) {
                    
                    //REMEMBER THE LAST MOVE INTO A LIST
                    listofActions.add(EAction.TurnRight);
                    
                    //ADDED TURN RIGHT TO THE ARRAYLIST
                    
          if(arrayinfo[0]==1)           
            {
                arrayinfo[1]=1;
                //here i should empty the array
                // arrayinfo[0]=0;
                // arrayinfo[1]=0;
                 //listofActions.clear();
            }else
            {
            arrayinfo[0]=1;//there is no ant in front of me, there is something blocking
            }
          
           //method for returning the action
                    System.out.println("I AM TURNING RIGHT AFTER I ENCOUNTERED A ROCK ");
                    return EAction.TurnRight;
//                } else if (possibleActions.contains(EAction.TurnRight)) {
//                    return EAction.TurnRight;
                } else {
                    System.out.println("I AM PAASING AFTER ENCOUNTER A BLOCK ");
                    return EAction.Pass;
                }
            }
        }
//***************************************************************************************************************888
    
    else if (visibleLocations.isEmpty ()) {
            System.out.println("I ENCOUNTERED A WALL ");
               int leftOrMoveBackward = gen.nextInt(3) + 1;
        if (leftOrMoveBackward == 1 && possibleActions.contains(EAction.TurnLeft)) {
            
            System.out.println(" I AM TURNING LEFT AFTER ENCOUNTER END ");
            return EAction.TurnLeft;
        } else if (leftOrMoveBackward == 2 && possibleActions.contains(EAction.TurnRight)) {
            
            System.out.println(" I AM TURNING RIGHT AFTER ENCOUNTER END ");
            return EAction.TurnRight;
            
        } else if (possibleActions.contains(EAction.MoveBackward))//leftOrMoveBackward == 2
        {
            System.out.println(" I AM TURNING BACK AFTER ENCOUNTER END ");
            int leftOrRightMove = gen.nextInt(2) + 1;
            arrayLeftOrRightMove[0] = leftOrRightMove;
            
            
            
            
            return EAction.MoveBackward;
            
        } else {
            
            System.out.println("I AM PASSING AFTER ENCOUNTERED A WALL ");
            return EAction.Pass;
        }
    }
    else if (possibleActions.contains (EAction.Pass) 
        ) {
            
            return EAction.Pass;
    }
        
        
        
        
        if(action==null)
        {
            if(possibleActions.contains(EAction.Attack) && visibleLocations.get(0).getAnt().getTeamInfo().getTeamID() != thisAnt.getTeamInfo().getTeamID())
            {
                  return EAction.Attack;
            }else
            
            {   
           int genr = possibleActions.size();
           int pickedaction = gen.nextInt(genr);
            System.out.println(" THE ACTION HAPPENED TO BE NULL ");
           //action = EAction.Pass;
         return  action = possibleActions.get(pickedaction);
            }
        }
    return action ;
    }
    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
      
    }
    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        
        
        System.out.println("The enemy location is "+dir);
        if(dir==1 && thisAnt.getDirection()==3)
        {
            
        }else if(dir==3 && thisAnt.getDirection()==1)
        {
            
        }else if(dir==2 && thisAnt.getDirection()==1)
        {
            
        }else if(thisAnt.getDirection()==2 && dir==1)
        {
            
        }else
        {
         runaway = true;   
        }
        
        
        // if i cant see it 
        
//        if(thisAnt.getDirection()!=attacker.getDirection())
//        {
//           runaway = true;
//        }
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void onDeath(IAntInfo thisAnt) {
        
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
//    public EAction checkForAnotherBlockingCube(int[] array,List<EAction> list4)
//    { 
//        
//    }
}


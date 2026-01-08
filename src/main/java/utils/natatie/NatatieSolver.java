package utils.natatie;

import models.Duck;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NatatieSolver{

    public static double solve(NatatieDTO data){
        List<Duck> ducks = new ArrayList<>(data.getDucks());
        ducks.sort(Comparator.comparingDouble(Duck::getResistance));
        return maxTime(ducks,data.getDistances());
    }

    public static double maxTime(List<Duck> ducks,List<Integer> distances){
        double maxTime = -1d;
        for(int i = 0; i < ducks.size(); i++){
            double time = 2*distances.get(i)/ducks.get(i).getSpeed();
            if(time > maxTime){
                maxTime = time;
            }
        }
        return maxTime;
    }

}

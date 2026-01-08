package utils.natatie;

import models.Duck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NatatieDTO {

    private final List<Duck> ducks;
    private final List<Integer> distances = new ArrayList<>();

    public NatatieDTO(List<Duck> ducks) {
        this.ducks = ducks;
        generateDistances(ducks.size());
    }

    public List<Duck> getDucks() {
        return ducks;
    }

    public List<Integer> getDistances() {
        return distances;
    }
    private void generateDistances(int n) {
        Random random = new Random();
        int current = random.nextInt(10);

        for (int i = 0; i < n; i++) {
            current += random.nextInt(10) * 10 + 1;
            distances.add(current);
        }
    }
}

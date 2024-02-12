package it.uniba.dib.sms232417.fithub.entity;

public class Execise {

    private String name;
    private String muscleGroup;
    private int reps;
    private int sets;
    private int rest;

    public Execise(String name, String muscleGroup, int reps, int sets, int rest) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.reps = reps;
        this.sets = sets;
        this.rest = rest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    @Override
    public String toString() {
        return "Execise{" +
                "name='" + name + '\'' +
                ", muscleGroup='" + muscleGroup + '\'' +
                ", reps=" + reps +
                ", sets=" + sets +
                ", rest=" + rest +
                '}';
    }
}

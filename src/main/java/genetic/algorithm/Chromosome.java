package genetic.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Chromosome {
    private final Function<Chromosome, Float> fitnessFunction;
    private final Integer chromosomeSize;
    private Function<Chromosome, Float> decoder;
    private Float fitness;
    ArrayList<Integer> state;
    private Float rouletteValue;
    private List<Boolean> mask;


    public Chromosome(Integer chromosomeSize, Function<Chromosome, Float> fitnessFunction){
        this.fitnessFunction = fitnessFunction;
        this.chromosomeSize = chromosomeSize;
        this.state = new ArrayList<>();
        for(int i = 0; i<chromosomeSize; i++){
            this.state.add((int) (Math.random() * 2));
        }
    }

    @SafeVarargs
    public Chromosome(Function<Chromosome, Float> fitnessFunction, List<Integer>... l){
        this.fitnessFunction = fitnessFunction;
        this.state = new ArrayList<>(l[0]);
        for(int i = 1; i<l.length; i++){
            this.state.addAll(l[i]);
        }
        this.chromosomeSize = this.state.size();
    }

    public Chromosome(Function<Chromosome, Float> fitnessFunction, List<Integer> l){
        this.fitnessFunction = fitnessFunction;
        this.state = new ArrayList<>(l);
        this.chromosomeSize = this.state.size();
    }

    public static Collection<? extends Chromosome> generatePopulation(Integer populationSize, Integer chromosomeSize, Function<Chromosome, Float> fitnessFunction) {
        ArrayList<Chromosome> population = new ArrayList<>();
        for(int i = 0; i<populationSize; i++){
            population.add(new Chromosome(chromosomeSize, fitnessFunction));
        }
        return population;
    }

    public ArrayList<Integer> getState(){
        return this.state;
    }

    public Float getFitness() {
        return this.fitness;
    }

    public void computeFitness() {
        this.fitness = this.fitnessFunction.apply(this);
    }

    public void printFitness(Integer i) {
        System.out.printf("Individual %d -> fitness %f%n", i, fitness);
    }

    public void setDecoder(Function<Chromosome, Float> decoder){
        this.decoder = decoder;
    }

    public Float decode(){
        return this.decoder.apply(this);
    }

    public Float getRouletteValue() {
        return rouletteValue;
    }

    public void setRouletteValue(Float rouletteValue) {
        this.rouletteValue = rouletteValue;
    }

    public void setMask(List<Boolean> mask){
        this.mask = mask;
    }

    public List<Boolean> getMask(){
        return this.mask;
    }

    Chromosome createMutant(Float probabilityOfMutation){
        List<Integer> mutant = new ArrayList<>(this.state);
        for(int i = 0; i<this.chromosomeSize; i++){
            if(Math.random() < probabilityOfMutation){
                mutant.set(i, 1 - mutant.get(i));
            }
        }

        return new Chromosome(this.fitnessFunction, mutant);
    }

    public void mutate(Float probabilityOfMutation){
        this.state = createMutant(probabilityOfMutation).state;
    }

    Chromosome[] crossover(Chromosome other, String crossoverType){
        switch (crossoverType) {
            case "one-point" -> {
                return onePointCrossover(other);
            }
            case "two-point" -> {
                return twoPointCrossover(other);
            }
            case "uniform" -> {
                return uniformCrossover(other);
            }
            case "masked-uniform" -> {
                return uniformCrossover(other, mask);
            }
            default -> {
                return new Chromosome[]{this, other};
            }
        }
    }

    Chromosome[] onePointCrossover(Chromosome other){
        List<Integer> p1 = this.state.subList(0, this.chromosomeSize/2);
        List<Integer> p2 =this.state.subList(this.chromosomeSize/2, this.chromosomeSize);
        List<Integer> p3 =other.state.subList(0, this.chromosomeSize/2);
        List<Integer> p4 =other.state.subList(this.chromosomeSize/2, this.chromosomeSize);
        return new Chromosome[]{new Chromosome(this.fitnessFunction, p1, p4), new Chromosome(this.fitnessFunction, p3, p2)};
    }

    Chromosome[] twoPointCrossover(Chromosome other){
        List<Integer> p1 = this.state.subList(0, this.chromosomeSize/3);
        List<Integer> p2 =this.state.subList(this.chromosomeSize/3, 2*this.chromosomeSize/3);
        List<Integer> p3 =this.state.subList(2*this.chromosomeSize/3, this.chromosomeSize);
        List<Integer> p4 =other.state.subList(0, this.chromosomeSize/3);
        List<Integer> p5 =other.state.subList(this.chromosomeSize/3, 2*this.chromosomeSize/3);
        List<Integer> p6 =other.state.subList(2*this.chromosomeSize/3, this.chromosomeSize);
        return new Chromosome[]{new Chromosome(this.fitnessFunction, p1, p5, p3), new Chromosome(this.fitnessFunction, p4, p2, p6)};
    }

    Chromosome[] uniformCrossover(Chromosome other){
        List<Integer> p1 = new ArrayList<>();
        List<Integer> p2 = new ArrayList<>();
        for(int i = 0; i<this.chromosomeSize; i++){
            if(Math.random() < 0.5){
                p1.add(this.state.get(i));
                p2.add(other.state.get(i));
            }else{
                p1.add(other.state.get(i));
                p2.add(this.state.get(i));
            }
        }
        return new Chromosome[]{new Chromosome(this.fitnessFunction, p1), new Chromosome(this.fitnessFunction, p2)};
    }

    Chromosome[] uniformCrossover(Chromosome other, List<Boolean> mask){
        List<Integer> p1 = new ArrayList<>();
        List<Integer> p2 = new ArrayList<>();
        for(int i = 0; i<this.chromosomeSize; i++){
            if(mask.get(i)){
                p1.add(this.state.get(i));
                p2.add(other.state.get(i));
            }else{
                p1.add(other.state.get(i));
                p2.add(this.state.get(i));
            }
        }
        return new Chromosome[]{new Chromosome(this.fitnessFunction, p1), new Chromosome(this.fitnessFunction, p2)};
    }

}

package core;

import java.util.ArrayList;
import java.util.Collections;

import javax.xml.crypto.KeySelector.Purpose;

import tools.*;
import javafx.scene.canvas.GraphicsContext;

/** Classe de la population de robots genetique
 * 
 * @author DHT
 *
 */
public class Population {
	
	/** Le plateau sur lequel évolue la population*/
	private Plateau plateau;
	/** L'ensemble de robot de la population */
	private ArrayList<GeneticRobot> population = new ArrayList<GeneticRobot>();
	
	/** Taille de la population */
	private int size;
	
	public Population(Plateau plateau, int size){
		this.plateau = plateau;
		this.size = size;
		for (int i=0; i<this.size; i++){
			population.add(new GeneticRobot(plateau,i));
		}
	}
	
	public Population(Plateau plateau, ArrayList<GeneticRobot> robots){
		this.plateau = plateau;
		this.population = robots;
		this.size = robots.size();
		
	}

	public void update(double dt) {
		for (Robot r : population)
			if (!r.isDead())
				r.update(dt);	
	}
	
	public void draw(GraphicsContext g) {
		for (Robot r : population)
			r.draw(g);
	}
	
	/** Cree la generation suivante d'individus
	 * 
	 * @return la generation suivant
	 */
	public Population nextGeneration(Plateau plateau) {
		this.plateau = plateau;
		// Get the best GeneticRobots
		ArrayList<GeneticRobot> parents = selection();
		Debug.log.println("Selection : "+ parents.size() +" indiv");
		// Cross their brain
		ArrayList<GeneticRobot> childs = mutate(crossover(parents));
		Debug.log.println("Crossover : "+ childs.size() +" indiv");
		// Create the new population for the following generation
		parents.addAll(childs);
		Debug.log.println("Nouvelle population : "+parents.size()+" indiv");
		Population newPop = new Population(plateau, parents);
		
		return newPop;
	}
	
	/** Selectionne les meilleurs individus
	 * 
	 * @return les individus selectionne
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<GeneticRobot> selection(){
		// Tri la population par ordre decroissant de score
		Collections.sort(population);
		String deb_fitness = "Max Fitness => ";
		for (int i = 1; i < 5; i++) {
			deb_fitness+= "(" +population.get(i-1).getScore() +","+ population.get(i-1).getNbFoundObj()+")   ";
		}
		Debug.log.println(deb_fitness);
		// Selection des keep_proba pourcent meilleurs parents
		int SIZE_PARENTS = (int)(Parametre.KEEP_PROBA*this.size);
		ArrayList<GeneticRobot> parents = new ArrayList<GeneticRobot>();
		for (int i = 0; i < SIZE_PARENTS; i++) {
			parents.add(new GeneticRobot(this.plateau, population.get(i).getBrain(),i));
		}
		return parents;
	}
	
	/** Croise les individus
	 * 
	 * @param parents les individus de la generation precedente
	 * @return les nouveaux individus issus de croisements genetiques
	 */
	private ArrayList<GeneticRobot> crossover(ArrayList<GeneticRobot> parents) {
		ArrayList<GeneticRobot> childs = new ArrayList<GeneticRobot>();
		int SIZE_PARENTS = (int)(Parametre.KEEP_PROBA*this.size);
		
		for (int child = SIZE_PARENTS; child < this.size; child++) {
			IA newBrain = null;
			if (Math.random() <= Parametre.CROSS_RATE) {
				// Recupere 2 parents aleatoirement et croise leurs genes
				int p1 = Outils.RAND.nextInt(parents.size()), p2 = Outils.RAND.nextInt(parents.size());
				newBrain = ((GeneticBrain)parents.get(p1).getBrain()).cross((GeneticBrain)parents.get(p2).getBrain());
			}
			else {
				newBrain = parents.get(Outils.RAND.nextInt(parents.size())).getBrain();
			}
			childs.add(new GeneticRobot(this.plateau, newBrain, child));
		}
		return childs;
	}
	
	/** Mute les genes des enfants
	 * 
	 * @param childs les enfants
	 * @return les enfants mutes
	 */
	private ArrayList<GeneticRobot> mutate(ArrayList<GeneticRobot> childs) {
		ArrayList<GeneticRobot> mutate_childs = new ArrayList<GeneticRobot>();
		int SIZE_PARENTS = (int)(Parametre.KEEP_PROBA*this.size);
		
		for (int child = 0; child < childs.size(); child++) {
			IA mutate_brain = ((GeneticBrain)childs.get(child).getBrain()).mutate(Parametre.MUTATION_RATE);
			mutate_childs.add(new GeneticRobot(this.plateau, mutate_brain, child+SIZE_PARENTS));
		}
		return mutate_childs;
	}
	
	public boolean allDead() {
		for(Robot r : population) {
			if (!r.isDead())
				return false;
		}
		return true;
	}
	
	public Robot getRobot(int num) {
		return this.population.get(num);
	}
	public ArrayList<GeneticRobot> getRobots() {
		return this.population;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void setPlateau(Plateau plateau) {
		this.plateau = plateau;
	}
	
}

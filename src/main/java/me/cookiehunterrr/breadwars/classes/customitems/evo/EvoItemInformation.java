package me.cookiehunterrr.breadwars.classes.customitems.evo;

import java.io.Serializable;

public class EvoItemInformation implements Serializable
{
    private int evoID;
    private double experience;
    private int currentStage;
    private int specialty;

    public EvoItemInformation(int evoID)
    {
        this.evoID = evoID;
        this.experience = 0;
        this.specialty = 0;
    }

    public int getEvoID() { return evoID; }

    public double getExperience() { return experience; }
    public void addExperience(double experience) { this.experience += experience; }

    public int getCurrentStage() { return currentStage; }
    public void setCurrentStage(int currentStage) { this.currentStage = currentStage; }

    public int getSpecialty() { return specialty; }
    public void setSpecialty(int specialty) { this.specialty = specialty; }
}
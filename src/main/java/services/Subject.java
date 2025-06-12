/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import models.Voyage;

/**
 *
 * @author enesaltin
 */
public interface Subject {
    void notifyObservers(Voyage voyage);
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}

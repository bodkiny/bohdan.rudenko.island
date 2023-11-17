package ua.javarush.living_beings.animals;

import ua.javarush.interfaces.Feeding;
import ua.javarush.interfaces.Moving;
import ua.javarush.living_beings.LivingBeing;

public abstract class Animal extends LivingBeing implements Feeding, Moving {
}

package ua.javarush.living_beings;

import lombok.Getter;
import lombok.Setter;
import ua.javarush.interfaces.Populating;
import ua.javarush.island.Location;
import ua.javarush.util.factories.LivingBeingFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Setter
@Getter
public abstract class LivingBeing implements Populating {
    private String icon;
    private String name;
    private boolean hasAlreadyReproduced;
    private int maxPopulation;

    @Override
    public <T extends LivingBeing> T populate(Location location) {
        if(!hasAlreadyReproduced){
            List<LivingBeing> sameSpecies = location.getLivingBeings().get(getClass());
            if(sameSpecies.size() < maxPopulation){
                Optional<LivingBeing> partner = getPartner(sameSpecies);
                if(partner.isPresent()){
                    T child = (T) LivingBeingFactory.getLivingBeingByClass(getClass());
                    partner.get().setHasAlreadyReproduced(true);
                    child.setHasAlreadyReproduced(true);
                    return child;
                }
            }
        }
        return null;
    }

    private Optional<LivingBeing> getPartner(List<LivingBeing> sameSpecies) {
        return sameSpecies.stream().
                filter(Predicate.not(LivingBeing::hasAlreadyReproduced))
                .findAny();
    }

    public boolean hasAlreadyReproduced(){
        return hasAlreadyReproduced;
    }
}

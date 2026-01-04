package com.group.Timetable.Generator.ga;

import lombok.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GAChromosome implements Comparable<GAChromosome> {
    private List<GAGene> genes = new ArrayList<>();
    private double fitness = Double.NEGATIVE_INFINITY;

    public GAChromosome(List<GAGene> genes) {
        this.genes = new ArrayList<>(genes);
    }

    public GAChromosome copy() {
        List<GAGene> copy = new ArrayList<>();
        for (GAGene g : genes) {
            copy.add(new GAGene(g.getSectionId(), g.getSubjectId(), g.getTeacherId(), g.getRoomId(), g.getTimeSlotId()));
        }
        GAChromosome c = new GAChromosome(copy);
        c.setFitness(this.fitness);
        return c;
    }

    @Override
    public int compareTo(GAChromosome o) {
        return Double.compare(o.getFitness(), this.getFitness()); // descending (higher fitness first)
    }
}


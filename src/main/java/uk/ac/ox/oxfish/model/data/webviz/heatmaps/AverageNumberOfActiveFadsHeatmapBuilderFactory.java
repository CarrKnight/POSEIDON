/*
 *  POSEIDON, an agent-based model of fisheries
 *  Copyright (C) 2020  CoHESyS Lab cohesys.lab@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package uk.ac.ox.oxfish.model.data.webviz.heatmaps;

import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.data.webviz.JsonBuilderFactory;
import uk.ac.ox.oxfish.model.data.webviz.scenarios.ColourMapEntry;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class AverageNumberOfActiveFadsHeatmapBuilderFactory implements HeatmapBuilderFactory {

    private int interval = 30;
    private GradientColourMapBuilderFactory colourMapBuilderFactory =
        new GradientColourMapBuilderFactory();

    public AverageNumberOfActiveFadsHeatmapBuilderFactory() {
        colourMapBuilderFactory.setMaxValue(10);
        colourMapBuilderFactory.setMaxColour("yellow");
    }

    @Override public String getTitle() { return "Average number of active FADs"; }

    @Override public JsonBuilderFactory<Collection<ColourMapEntry>> getColourMapBuilderFactory() {
        return this.colourMapBuilderFactory;
    }

    @SuppressWarnings("unused")
    public void setColourMapBuilderFactory(GradientColourMapBuilderFactory colourMapBuilderFactory) {
        this.colourMapBuilderFactory = colourMapBuilderFactory;
    }

    @Override public ToDoubleFunction<SeaTile> makeNumericExtractor(FishState fishState) {
        return seaTile -> fishState.getFadMap().fadsAt(seaTile).numObjs;
    }

    @Override public TimestepsBuilder makeTimestepsBuilder() {
        return new AveragingAtIntervalTimestepsBuilder(interval);
    }

    @SuppressWarnings("unused") public int getInterval() { return interval; }

    @SuppressWarnings("unused") public void setInterval(int interval) { this.interval = interval; }

}
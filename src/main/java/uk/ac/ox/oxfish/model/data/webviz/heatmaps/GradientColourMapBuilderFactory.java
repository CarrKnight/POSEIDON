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

import com.google.common.collect.ImmutableList;
import uk.ac.ox.oxfish.model.data.webviz.JsonBuilder;
import uk.ac.ox.oxfish.model.data.webviz.JsonDefinitionBuilderFactory;
import uk.ac.ox.oxfish.model.data.webviz.scenarios.ColourMapEntry;

import java.util.Collection;
import java.util.function.DoubleSupplier;

import static uk.ac.ox.oxfish.model.data.webviz.colours.ColourUtils.colourStringToHtmlCode;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class GradientColourMapBuilderFactory implements JsonDefinitionBuilderFactory<Collection<ColourMapEntry>> {

    private String minColour = "green";
    private String maxColour = "green";
    private double minOpacity = 0.00;
    private double maxOpacity = 0.75;
    private DoubleSupplier minValueFunction = () -> 0;
    private DoubleSupplier maxValueFunction = () -> 1;

    public DoubleSupplier getMinValueFunction() { return minValueFunction; }

    public void setMinValueFunction(final DoubleSupplier minValueFunction) {
        this.minValueFunction = minValueFunction;
    }

    public DoubleSupplier getMaxValueFunction() { return maxValueFunction; }

    public void setMaxValueFunction(final DoubleSupplier maxValueFunction) {
        this.maxValueFunction = maxValueFunction;
    }

    public String getMinColour() { return minColour; }

    public void setMinColour(final String minColour) { this.minColour = minColour; }

    public String getMaxColour() { return maxColour; }

    public void setMaxColour(final String maxColour) { this.maxColour = maxColour; }

    public double getMinOpacity() { return minOpacity; }

    public void setMinOpacity(final double minOpacity) { this.minOpacity = minOpacity; }

    public double getMaxOpacity() { return maxOpacity; }

    public void setMaxOpacity(final double maxOpacity) { this.maxOpacity = maxOpacity; }

    /**
     * Colour maps do not have their own file names
     */
    @Override public String getBaseName() { throw new UnsupportedOperationException(); }

    @Override public JsonBuilder<Collection<ColourMapEntry>> makeDefinitionBuilder(final String scenarioTitle) {
        return fishState -> ImmutableList.of(
            new ColourMapEntry(
                minValueFunction.getAsDouble(),
                colourStringToHtmlCode(minColour),
                minOpacity,
                true
            ),
            new ColourMapEntry(
                maxValueFunction.getAsDouble(),
                colourStringToHtmlCode(maxColour),
                maxOpacity,
                true
            )
        );
    }

}

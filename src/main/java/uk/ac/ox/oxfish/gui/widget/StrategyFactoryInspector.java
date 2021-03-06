/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2017  CoHESyS Lab cohesys.lab@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package uk.ac.ox.oxfish.gui.widget;

import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.util.CollectionUtils;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * The "MetaInspector" (in the metawidget sense, not the mason sense) that looks for AlgorithmFactories.
 * If it finds one it adds a "factory" attribute to the element
 * Created by carrknight on 5/29/15.
 */
public class StrategyFactoryInspector  extends BaseObjectInspector
{


    /**
     * Inspect the given property and return a Map of attributes.
     * <p>
     * Note: for convenience, this method does not expect subclasses to deal with DOMs and Elements.
     * Those subclasses wanting more control over these features should override methods higher in
     * the call stack instead.
     * <p>
     * Does nothing by default.
     *
     * @param property the property to inspect
     */
    @Override
    protected Map<String, String> inspectProperty(Property property) throws Exception {

        Map<String, String> attributes = CollectionUtils.newHashMap();



        //turn String into Class object, if possible
        if(property.isWritable()) {
            try {
                final Class<?> propertyClass = Class.forName(property.getType());
                if (AlgorithmFactory.class.isAssignableFrom(propertyClass)) {

                    //it is a strategy factory!
                    //now most of the time it should be something like factory<? extends x>
                    //with getGenericType() we get ? extends x, but we want only x
                    //so we split and take last
                    String[] splitType;
                    if(property.getGenericType()!=null)
                    {
                        splitType = FishStateUtilities.removeParentheses(
                                property.getGenericType()).split(" ");
                    }
                    else{
                        ParameterizedType type = (ParameterizedType)(propertyClass.getGenericInterfaces()[0]);
                        splitType = FishStateUtilities.removeParentheses(type.getActualTypeArguments()[0].toString()).split(" ");
                    }
                 //   if(Log.TRACE)
               //         Log.trace("analyzed a strategy factory and put : '" + splitType[splitType.length - 1] + "' in" );
                    //store it as attribute factory_strategy="x" which we will use to build widgets on
                    attributes.put("factory_strategy", splitType[splitType.length - 1]);

                }
            } catch (ClassNotFoundException e) {
                //this can happen (think primitives)
            }
        }
        return attributes;

    }
}

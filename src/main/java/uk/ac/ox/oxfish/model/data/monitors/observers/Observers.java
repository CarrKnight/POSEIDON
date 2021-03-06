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

package uk.ac.ox.oxfish.model.data.monitors.observers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class Observers {

    private final Multimap<Class<?>, Observer<?>> observers = HashMultimap.create();

    public <T> void register(Class<T> observedClass, Observer<? super T> observer) {
        this.observers.put(observedClass, observer);
    }

    public void unregister(Observer<?> observer) {
        // copy the keys to avoid ConcurrentModificationException
        final ImmutableList<Class<?>> observedClasses = ImmutableList.copyOf(observers.keySet());
        observedClasses.forEach(observedClass -> unregister(observedClass, observer));
    }

    public void unregister(Class<?> observedClass, Observer<?> observer) {
        this.observers.remove(observedClass, observer);
    }

    public <O> void reactTo(O observable) {
        //noinspection unchecked
        this.observers
            .get(observable.getClass())
            .forEach(observer -> ((Observer<O>) observer).observe(observable));
    }

}

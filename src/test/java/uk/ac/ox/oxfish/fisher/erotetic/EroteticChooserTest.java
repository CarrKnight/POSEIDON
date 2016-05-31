package uk.ac.ox.oxfish.fisher.erotetic;

import org.junit.Test;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by carrknight on 4/11/16.
 */
public class EroteticChooserTest
{

    @Test
    public void chooseCorrectly() throws Exception
    {

        SeaTile one =mock(SeaTile.class);
        SeaTile two =mock(SeaTile.class);
        SeaTile three =mock(SeaTile.class);
        List<SeaTile> toChoose = Arrays.asList(one,two,three);

        EroteticAnswer<SeaTile> fakeFilterOne = mock(EroteticAnswer.class);
        List<SeaTile> fakeChoiceOne = Collections.singletonList(one);
        when(fakeFilterOne.answer(anyList(), any(), any(), any())).thenReturn(fakeChoiceOne);


        EroteticAnswer<SeaTile> fakeFilterTwo = mock(EroteticAnswer.class);
        List<SeaTile> fakeChoiceTwo = Collections.singletonList(two);
        when(fakeFilterTwo.answer(anyList(), any(), any(), any())).thenReturn(fakeChoiceTwo);

        EroteticChooser<SeaTile> chooser = new EroteticChooser<>();
        //priority to one, one should be choosen
        chooser.add(fakeFilterOne);
        chooser.add(fakeFilterTwo);
        assertEquals(
                one,
                chooser.answer(toChoose,
                               mock(FeatureExtractors.class),
                               mock(FishState.class), mock(Fisher.class))
        );


        //if I put priority to two, two should be chosen
        chooser = new EroteticChooser<>();
        //priority to one, one should be choosen
        chooser.add(fakeFilterTwo);
        chooser.add(fakeFilterOne);
        assertEquals(
                two,
                chooser.answer(toChoose,
                               mock(FeatureExtractors.class),
                               mock(FishState.class), mock(Fisher.class))
        );



    }

    @Test
    public void getFirst() throws Exception {
        SeaTile one =mock(SeaTile.class);
        SeaTile two =mock(SeaTile.class);
        SeaTile three =mock(SeaTile.class);
        List<SeaTile> toChoose = Arrays.asList(one,two,three);

        EroteticAnswer<SeaTile> fakeFilterOne =
                new EroteticAnswer<SeaTile>() {
                    @Override
                    public List<SeaTile> answer(
                            List<SeaTile> currentOptions, FeatureExtractors<SeaTile> representation,
                            FishState state, Fisher fisher) {
                        LinkedList<SeaTile> choices = new LinkedList<>(currentOptions);
                        choices.remove(0);
                        return choices;
                    }

                    @Override
                    public void start(FishState model) {

                    }

                    @Override
                    public void turnOff() {

                    }
                };

        EroteticChooser<SeaTile> chooser = new EroteticChooser<>();
        //priority to one, one should be choosen
        chooser.add(fakeFilterOne);
        chooser.add(fakeFilterOne);
        assertEquals(
                three,
                chooser.answer(toChoose,
                               mock(FeatureExtractors.class),
                               mock(FishState.class), mock(Fisher.class))
        );



    }


}
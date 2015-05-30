package uk.ac.ox.oxfish.gui.widget;

import org.apache.commons.beanutils.PropertyUtils;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.widgetprocessor.iface.WidgetProcessor;
import uk.ac.ox.oxfish.utility.StrategyFactories;
import uk.ac.ox.oxfish.utility.StrategyFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * This class looks for factory_strategy attributes and if it finds them it creates a combo-box so
 * users can change the scenario factory used
 * Created by carrknight on 5/29/15.
 */
public class StrategyWidgetProcessor implements WidgetProcessor<JComponent,SwingMetawidget>
{

    /**
     * Process the given widget. Called after a widget has been built by the
     * <code>WidgetBuilder</code>, and before it is added to the <code>Layout</code>.
     *
     * @param widget      the widget to process. Never null
     * @param elementName XML node name of the business field. Typically 'entity', 'property' or 'action'.
     *                    Never null
     * @param attributes  attributes of the widget to process. Never null. This Map is modifiable - changes
     *                    will be passed to subsequent WidgetProcessors and Layouts
     * @param metawidget  the parent Metawidget. Never null
     * @return generally the original widget (as passed in to the first argument). Can be a
     * different widget if the WidgetProcessor wishes to substitute the original widget for
     * another. Can be null if the WidgetProcessor wishes to cancel all further processing
     * of this widget (including laying out)
     */
    @Override
    public JComponent processWidget(
            JComponent widget, String elementName, Map<String, String> attributes, SwingMetawidget metawidget)
    {

        try {
            if (attributes.containsKey("factory_strategy"))
            {
                //find it what are you building
                Class strategyClass = Class.forName(attributes.get("factory_strategy"));
                //get list of constructors
                Map<String,? extends StrategyFactory> constructors = StrategyFactories.CONSTRUCTOR_MAP.get(strategyClass);

                //build JComponent
                final JComboBox<String> factoryBox = new JComboBox<>();
                //fill it with the strings from the constructor masterlist
                for(String title : constructors.keySet())
                    factoryBox.addItem(title);
                factoryBox.setSelectedIndex(-1); //have none selected
                //gui layout and panelling:
                JPanel panel = new JPanel();
                BoxLayout layout = new BoxLayout(panel,BoxLayout.PAGE_AXIS);
                panel.setLayout(layout);
                panel.add(factoryBox);
                panel.add(new JSeparator());
                panel.add(new JLabel(attributes.get("actual-class")));
                panel.add(widget);

                //now listen carefully to combobx
                factoryBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //we need to make changes!
                        try {
                            //use the beansutils to set the new value to the field
                            PropertyUtils.setSimpleProperty(
                                    //the object to modify
                                    metawidget.getToInspect(),
                                    //the name of the field
                                    attributes.get("name"),
                                    //the new value (table lookup)
                                    constructors.get((String) factoryBox.getSelectedItem()));

                            //now update the gui
                            //for some reason rebind alone is not enough here (although it is strange because it works elsewhere for the same change)
                            //metawidget.getWidgetProcessor(BeanUtilsBindingProcessor.class).rebind(metawidget.getToInspect(),metawidget);

                            //so i bind it again by setter
                            metawidget.setToInspect(metawidget.getToInspect());
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                            System.err.print("failed to find class! " + e1);
                            e1.printStackTrace();
                        }


                    }
                });

                return panel;
            }
        }
        catch (ClassNotFoundException c){
            System.err.print("failed to find class! " + c);
            c.printStackTrace();

        }



        return widget;
    }
}

package yang.preWork.text;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.checkerframework.checker.nullness.qual.Nullable;


import javax.print.DocFlavor;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileDealer {

    private final String TYPE_DESC = "charOrderDESC";
    private final String TYPE_NATURE = "natureOrder";
    private final String TYPE_INDEX = "indexOrder";
    private final String TYPE_ASC = "charOrder";

    private Map<Integer, String> natureIndex = Maps.newHashMap();
    private List<String> natureOrder;
    private List<String> ascOrder;
    private List<String> descOrder;
    private Splitter splitter = Splitter.on("\n").omitEmptyStrings().trimResults();

    public void initIndex() {


        try {

            String word = Resources.toString(new URL("http://qfc.qunar.com/homework/sdxl_prop.txt"),Charsets.UTF_8);

            List<String> words = Lists.newArrayList(splitter.split(word));

            natureOrder = Lists.transform(words, new Function<String, String>() {

                public String apply(@Nullable String input) {
                    List<String> temp = Lists.newArrayList(Splitter.on("\t").omitEmptyStrings().trimResults().split(input));
                    natureIndex.put(Integer.valueOf(temp.get(0)),temp.get(1));
                    return temp.get(1);
                }
            });

            ascOrder = Lists.newArrayList(natureOrder);
            Collections.sort(ascOrder);
            descOrder = Lists.newArrayList(ascOrder);
            Collections.reverse(descOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dealText() {
        try {
            String word = Resources.toString(new URL("http://qfc.qunar.com/homework/sdxl_template.txt"), Charsets.UTF_8);

            List<String> words = Lists.newArrayList(splitter.split(word));

            List<String> resultWord = Lists.transform(words, new Function<String, String>() {
                public String apply(@Nullable String input) {
                    if (CharMatcher.is('$').matchesAnyOf(input)) {
                        int number = Integer.valueOf(CharMatcher.inRange('0','9').retainFrom(input));
                        if (input.contains(TYPE_DESC)) {
                            return replace(input,descOrder.get(number));
                        } else if (input.contains(TYPE_ASC)) {
                            return replace(input,ascOrder.get(number));
                        } else if (input.contains(TYPE_INDEX)) {
                            return replace(input,natureIndex.get(number));
                        } else if (input.contains(TYPE_NATURE)) {
                            return replace(input,natureOrder.get(number));
                        }
                    }
                    return input;
                }
            });

            File file = new File("sdxl.txt");
            CharSink sink = Files.asCharSink(file,Charsets.UTF_8);
            sink.writeLines(resultWord);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replace(String resource, String word) {
        String[] words = resource.split("\\$.+.\\)");
        if (words.length == 1) {
            if (resource.startsWith("$")) {
                return word + words[0];
            } else {
                return words[0] + word;
            }
        }
        return words[0] + word + words[1];
    }
}

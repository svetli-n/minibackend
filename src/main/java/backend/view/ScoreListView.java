package backend.view;

import java.util.List;
import java.util.Map;

public class ScoreListView <K, V>  {

    public String format(List<Map.Entry<K, V>> scoreList) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : scoreList) {
            sb.append(entry.getValue());
            sb.append("=");
            sb.append(entry.getKey());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }


}

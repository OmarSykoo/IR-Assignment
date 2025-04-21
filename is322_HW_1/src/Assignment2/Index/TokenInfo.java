package Assignment2.Index;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfo {
    int DF;

    public TokenInfo() {
        DF = 0;
    }

    public void inc() {
        DF++;
    }
}

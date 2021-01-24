package com.borjamoll.amazon.data;

import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class Search {
    private String key;
    private int total;
    private boolean isPrime;
    private boolean save;
    private boolean read;
    private String url;


}

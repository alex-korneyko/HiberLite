package ua.in.korneiko.hiberlite;

import org.jetbrains.annotations.Contract;

public interface Entity {

    int getId();

    void setId(int id);

    @Contract(value = "null -> false", pure = true)
    boolean equals(Object o);
}

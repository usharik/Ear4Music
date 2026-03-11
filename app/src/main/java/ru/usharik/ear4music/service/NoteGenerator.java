package ru.usharik.ear4music.service;

import ru.usharik.ear4music.NotesEnum;

/**
 * Created by au185034 on 20/02/2018.
 */

public interface NoteGenerator {
    NotesEnum nextNote();
    boolean hasNextNote();
}

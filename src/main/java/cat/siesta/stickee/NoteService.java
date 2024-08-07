package cat.siesta.stickee;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note create(Note note) {
        return noteRepository.save(note);
    }

    public Optional<Note> get(String resourceLocator) {
        return noteRepository.findByResourceLocator(resourceLocator);
    }
}

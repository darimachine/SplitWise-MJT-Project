package bg.sofia.uni.fmi.mjt.splitwise.model;

import java.util.List;
import java.util.Set;

public record Group(String groupName, Set<String> members, List<String> expenses) {
}

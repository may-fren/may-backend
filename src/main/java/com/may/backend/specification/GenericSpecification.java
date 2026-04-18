package com.may.backend.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GenericSpecification {

    private static final Set<String> PAGEABLE_PARAMS = Set.of("page", "size", "sort");

    private GenericSpecification() {
    }

    public static <T> Specification<T> build(Class<T> entityClass, Map<String, String> filters) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (PAGEABLE_PARAMS.contains(key) || value == null || value.isBlank()) {
                    continue;
                }

                Path<?> path = getPath(root, key);
                if (path == null) {
                    continue;
                }

                Class<?> fieldType = path.getJavaType();

                if (fieldType.equals(String.class)) {
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));
                } else if (fieldType.isEnum()) {
                    Object enumValue = parseEnum(fieldType, value);
                    if (enumValue != null) {
                        predicates.add(cb.equal(path, enumValue));
                    }
                } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    predicates.add(cb.equal(path, Long.valueOf(value)));
                } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    predicates.add(cb.equal(path, Integer.valueOf(value)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> Path<?> getPath(Root<T> root, String key) {
        try {
            if (key.contains(".")) {
                String[] parts = key.split("\\.");
                Path<?> path = root.get(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    path = path.get(parts[i]);
                }
                return path;
            }

            if (key.endsWith("Id")) {
                String relationName = key.substring(0, key.length() - 2);
                try {
                    return root.get(relationName).get("id");
                } catch (IllegalArgumentException ignored) {
                }
            }

            return root.get(key);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object parseEnum(Class<?> enumType, String value) {
        try {
            return Enum.valueOf((Class<Enum>) enumType, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

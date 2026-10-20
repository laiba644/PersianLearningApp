
package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.ChapterComponent;
import edu.persianlearningapp.domain.ComponentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StructureBO {

    private final IDALFacade dal;

    public StructureBO(IDALFacade dal) {
        if (dal == null) {
            throw new IllegalArgumentException("DAL facade is required.");
        }
        this.dal = dal;
    }

    public List<ChapterComponent> getStructure(int chapterId) {
        requirePositiveId(chapterId);
        requireChapterExists(chapterId);

        List<ChapterComponent> components =
                dal.findComponentsByChapterId(chapterId);

        if (components == null) {
            throw new BusinessException(
                    "Could not load the chapter structure.");
        }

        return components;
    }

    public void saveStructure(
            int chapterId,
            List<ChapterComponent> components) {

        requirePositiveId(chapterId);
        requireChapterExists(chapterId);

        if (components == null) {
            throw new BusinessException(
                    "Chapter components are required.");
        }

        List<ChapterComponent> validatedComponents =
                new ArrayList<ChapterComponent>();

        Set<String> usedTypes = new HashSet<String>();
        Set<Integer> usedOrders = new HashSet<Integer>();

        for (ChapterComponent component : components) {

            if (component == null) {
                throw new BusinessException(
                        "A chapter component cannot be null.");
            }

            String type = component.getComponentType();

            if (type == null || type.trim().isEmpty()) {
                throw new BusinessException(
                        "Component type is required.");
            }

            ComponentType componentType;

            try {
                componentType = ComponentType.valueOf(
                        type.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new BusinessException(
                        "Unsupported component type: " + type);
            }

            int displayOrder = component.getDisplayOrder();

            if (displayOrder <= 0) {
                throw new BusinessException(
                        "Display order must be a positive integer.");
            }

            if (!usedTypes.add(componentType.name())) {
                throw new BusinessException(
                        "A component type cannot be added more than once: "
                                + componentType.name());
            }

            if (!usedOrders.add(displayOrder)) {
                throw new BusinessException(
                        "Display order values must be unique.");
            }

            validatedComponents.add(
                    new ChapterComponent(
                            0,
                            chapterId,
                            componentType.name(),
                            displayOrder));
        }

        Collections.sort(
                validatedComponents,
                new Comparator<ChapterComponent>() {
                    @Override
                    public int compare(
                            ChapterComponent first,
                            ChapterComponent second) {
                        return Integer.compare(
                                first.getDisplayOrder(),
                                second.getDisplayOrder());
                    }
                });

        dal.replaceStructure(chapterId, validatedComponents);
    }

    private void requireChapterExists(int chapterId) {
        Chapter chapter = dal.findById(chapterId);

        if (chapter == null) {
            throw new BusinessException(
                    "Chapter not found: " + chapterId);
        }
    }

    private void requirePositiveId(int chapterId) {
        if (chapterId <= 0) {
            throw new BusinessException(
                    "Chapter id must be positive.");
        }
    }
}

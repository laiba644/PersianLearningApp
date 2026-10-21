CREATE DATABASE IF NOT EXISTS persian_learning_app_team
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE persian_learning_app_team;

CREATE TABLE IF NOT EXISTS chapters (
  chapter_id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chapter_components (
  component_id INT NOT NULL AUTO_INCREMENT,
  chapter_id INT NOT NULL,
  component_type VARCHAR(80) NOT NULL,
  display_order INT NOT NULL,
  PRIMARY KEY (component_id),
  UNIQUE KEY uq_chapter_component_order (chapter_id, display_order),
  CONSTRAINT fk_component_chapter FOREIGN KEY (chapter_id)
    REFERENCES chapters(chapter_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- UC-03: one optional content record per configured component.
CREATE TABLE IF NOT EXISTS component_contents (
  component_id INT NOT NULL,
  persian_content TEXT NULL,
  urdu_content TEXT NULL,
  PRIMARY KEY (component_id),
  CONSTRAINT fk_content_component FOREIGN KEY (component_id)
    REFERENCES chapter_components(component_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS glossary_entries (
  entry_id INT NOT NULL AUTO_INCREMENT,
  chapter_id INT NOT NULL,
  persian_word VARCHAR(255) NOT NULL,
  urdu_meaning VARCHAR(1000) NOT NULL,
    PRIMARY KEY (entry_id),
  UNIQUE KEY uq_glossary_chapter_word (chapter_id, persian_word),
  CONSTRAINT fk_glossary_chapter FOREIGN KEY (chapter_id)
    REFERENCES chapters(chapter_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- FR-7 also mentions exercises. Do not invent exercise columns/table until the
-- instructor confirms the exercise scope and required data.

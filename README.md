# be14-4th-x1-groo-be

## ERD

```mermaid
erDiagram
    user {
        INT id PK
        VARCHAR email
        VARCHAR oauth_provider
        VARCHAR oauth_id
        VARCHAR password
        DATETIME created_at
        VARCHAR role
        DATETIME birth
        VARCHAR nickname
        BOOLEAN is_deleted
    }

    background {
        INT id PK
        VARCHAR name
        VARCHAR image_url
    }

    category {
        INT id PK
        VARCHAR category
    }

    item {
        INT id PK
        VARCHAR name
        VARCHAR image_url
        INT category_id FK
        VARCHAR emotion
    }

    forest {
        INT id PK
        VARCHAR name
        VARCHAR month
        BOOLEAN is_public
        INT background_id FK
        INT user_id FK
    }

    user_item {
        INT id PK
        INT item_id FK
        INT user_id FK
        INT total_count
        INT placed_count
        INT forest_id FK
    }

    shared_forest {
        INT id PK
        INT user_id FK
        INT forest_id FK
    }

    mailbox {
        INT id PK
        VARCHAR content
        DATETIME created_at
        BOOLEAN is_deleted
        INT user_id FK
        INT forest_id FK
    }

    announcement {
        INT id PK
        INT admin_id FK
        VARCHAR title
        TEXT content
        DATETIME created_at
    }

    diary {
        INT id PK
        DATETIME created_at
        DATETIME updated_at
        TEXT content
        BOOLEAN is_published
        INT user_id FK
        INT forest_id FK
        VARCHAR weather
    }

    diary_emotion {
        INT id PK
        INT weight
        INT diary_id FK
        VARCHAR emotion
    }

    placement {
        INT id PK
        DECIMAL position_x
        DECIMAL position_y
        INT user_id FK
        INT user_item_id FK
    }

    item }o--|| category : belongs_to
    forest }o--|| background : uses
    forest }o--|| user : owned_by
    user_item }o--|| user : owned_by
    user_item }o--|| item : contains
    user_item }o--|| forest : placed_in
    shared_forest }o--|| user : viewer
    shared_forest }o--|| forest : shared_from
    mailbox }o--|| user : written_by
    mailbox }o--|| forest : posted_in
    announcement }o--|| user : created_by
    diary }o--|| user : written_by
    diary }o--|| forest : related_to
    diary_emotion }o--|| diary : analyzed_from
    placement }o--|| user : placed_by
    placement }o--|| user_item : uses

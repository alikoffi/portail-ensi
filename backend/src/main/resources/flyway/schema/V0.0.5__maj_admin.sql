-- Mise a jour des identifiants du compte administrateur
-- Mot de passe : ensi@2026 (hash bcrypt)
UPDATE utilisateur
SET password_hash = '$2a$10$mbCKed8QgQzs54sjANjgUezPLxIsbWqyFck2K5j5LSi5tTC5wjJ8e',
    email = 'ably84@gmail.com'
WHERE username = 'admin';

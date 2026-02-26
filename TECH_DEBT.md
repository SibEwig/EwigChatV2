# Technical Debt

## Architecture
- [ ] Убрать прямое обращение VM -> AuthRepository (вынести в UseCase)
- [ ] Пересмотреть формирование ChatState (упростить flow)
- [ ] Избавиться от split("_") при получении interlocutorId

## Performance
- [ ] Добавить in-memory cache для ProfilesRepository

## Future Improvements
- [ ] Реализовать avatar upload через Firebase Storage
- [ ] Добавить presence (online status)
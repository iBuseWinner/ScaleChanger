package ru.fennec.free.scaleChanger.scaleChanger.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface MainConfig {

    @AnnotationBasedSorter.Order(1)
    @ConfComments("Префикс плагина. Используется <prefix> во всех сообщениях")
    @ConfDefault.DefaultString("<yellow>ScaleChanger</yellow>")
    String prefix();

    @AnnotationBasedSorter.Order(2)
    @ConfComments("Сообщение, если нет прав")
    @ConfDefault.DefaultString("<prefix> <red>У Вас недостаточно прав!</red>")
    String noPermission();

    @AnnotationBasedSorter.Order(3)
    @ConfComments("Сообщение при изменении своих размеров")
    @ConfDefault.DefaultString("<prefix> <white>Вы установили <green>себе</green> размер <green><player_scale></green></white>")
    String changeYourScale();

    @AnnotationBasedSorter.Order(4)
    @ConfComments("Сообщение при изменении размеров другого игрока")
    @ConfDefault.DefaultString("<prefix> <white>Вы установили <green><player_name></green> размер <green><player_scale></green></white>")
    String changeOtherPlayerScale();

    @AnnotationBasedSorter.Order(5)
    @ConfComments("Сообщение при изменении размеров сущности")
    @ConfDefault.DefaultString("<prefix> <white>Вы установили <green><entity_name></green> (<green><entity_type></green>, <green><entity_uuid></green>) размер <green><entity_scale></green></white>")
    String changeEntityScale();

    @AnnotationBasedSorter.Order(6)
    @ConfComments("Минимальный размер, который можно установить")
    @ConfDefault.DefaultDouble(0.1)
    double minScale();

    @AnnotationBasedSorter.Order(7)
    @ConfComments("Максимальный размер, который можно установить")
    @ConfDefault.DefaultDouble(15)
    double maxScale();

    @AnnotationBasedSorter.Order(8)
    @ConfComments("Сообщение, если указан размер меньше минимального")
    @ConfDefault.DefaultString("<prefix> <red>Вы указали размер меньше минимального (<yellow><min_scale></yellow>)!</red>")
    String minScaleLimit();

    @AnnotationBasedSorter.Order(9)
    @ConfComments("Сообщение, если указан размер больше максимального")
    @ConfDefault.DefaultString("<prefix> <red>Вы указали размер больше максимального (<yellow><max_scale></yellow>)!</red>")
    String maxScaleLimit();

    @AnnotationBasedSorter.Order(10)
    @ConfComments("Сообщение, если игрок не найден")
    @ConfDefault.DefaultString("<prefix> <red>Игрок с ником <yellow><player_name></yellow> не найден! (Возможно, оффлайн или ник введён неверно)</red>")
    String playerNotFound();

    @AnnotationBasedSorter.Order(11)
    @ConfComments("Сообщение, если сущность не найдена")
    @ConfDefault.DefaultString("<prefix> <red>Сущность <yellow><entity_name></yellow> не найдена!</red>")
    String entityNotFound();

    @AnnotationBasedSorter.Order(12)
    @ConfComments("Сообщение о текущем размере игрока")
    @ConfDefault.DefaultString("<prefix> <white>Ваш текущий размер - <green><player_scale></green>!</white>")
    String infoAboutYourScale();

    @AnnotationBasedSorter.Order(13)
    @ConfComments("Сообщение о текущем размере другого игрока")
    @ConfDefault.DefaultString("<prefix> <white>Текущий размер игрока <green><player_name></green> - <green><player_scale></green>!</white>")
    String infoAboutPlayerScale();

    @AnnotationBasedSorter.Order(14)
    @ConfComments("Сообщение о текущем размере игрока")
    @ConfDefault.DefaultString("<prefix> <white>Текущий размер сущности <green><entity_name></green> (<green><entity_type></green>, <green><entity_uuid></green>) - <green><entity_scale></green>!</white>")
    String infoAboutEntityScale();

    @AnnotationBasedSorter.Order(15)
    @ConfComments("Сообщение со всеми командами")
    @ConfDefault.DefaultStrings({"<prefix> <white>Актуальный список команд плагина для Игроков:</white>",
            "  <green>/scalechanger info</green> <white>- информация о своём размере</white>",
            "  <green>/scalechanger help</green> <white>- показать данный список команд</white>",
            "  <green>/scalechanger set <значение></green> <white>- установить свой размер</white>",
            "  <green>/scalechanger add <значение></green> <white>- добавить свой размер</white>",
            "  <green>/scalechanger rem <значение></green> <white>- вычесть свой размер</white>",
            "  <green>/scalechanger player info <ник></green> <white>- информация об игроке</white>",
            "  <green>/scalechanger entity info <UUID/near></green> <white>- информация о сущности</white>",
            "  <green>/scalechanger player set <ник> <значение></green> <white>- установить размер игроку</white>",
            "  <green>/scalechanger player add <ник> <значение></green> <white>- добавить размер игроку</white>",
            "  <green>/scalechanger player rem <ник> <значение></green> <white>- вычесть размер у игрока</white>",
            "  <green>/scalechanger entity set <UUID/near> <значение></green> <white>- установить размер сущности</white>",
            "  <green>/scalechanger entity add <UUID/near> <значение></green> <white>- добавить размер сущности</white>",
            "  <green>/scalechanger entity rem <UUID/near> <значение></green> <white>- вычесть размер у сущности</white>"})
    List<String> helpPlayerStrings();

    @AnnotationBasedSorter.Order(16)
    @ConfComments("Сообщение со всеми командами")
    @ConfDefault.DefaultStrings({"<prefix> <white>Актуальный список команд плагина для Администрации:</white>",
            "  <green>/scalechanger reload</green> <white>- перезагрузить конфиг плагина</white>"})
    List<String> helpAdminStrings();

    @AnnotationBasedSorter.Order(17)
    @ConfComments("Сообщение если команда доступна только из игры")
    @ConfDefault.DefaultString("<prefix> <red>Данная команда доступна только из игры!</red>")
    String onlyInGame();

    @AnnotationBasedSorter.Order(18)
    @ConfComments("Сообщение если в аргументе ввели не число")
    @ConfDefault.DefaultString("<prefix> <red>Введённое вами значение <yellow><arg></yellow> должно быть числом!</red>")
    String mustBeNumber();

    @AnnotationBasedSorter.Order(19)
    @ConfComments("Сообщение когда плагин перезагружен")
    @ConfDefault.DefaultString("<prefix> <white>Вы перезагрузили плагин!</white>")
    String reloadPlugin();

}

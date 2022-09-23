package me.refactoring.study._37_hide_delegate;


class PersonTest {

    @Test
    void manager() {
        Person keesun = new Person("keesun");
        Person nick = new Person("nick");
        keesun.setDepartment(new Department("m365deploy", nick));

        Person manager = keesun.getManager(keesun);
        assertEquals(nick, manager);
    }

}

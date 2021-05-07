/*
   Copyright 2009-2021 PrimeTek.

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.primefaces.paradise.view;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class GuestPreferences implements Serializable {

    private String layout = "default";

    private String menuMode = "layout-menu-static";

    private boolean darkMenu = false;

    private String theme = "blue";

    private String inputStyle = "outlined";

    private List<ComponentTheme> componentThemes;

    private List<FlatLayout> flatLayouts;

    private List<SpecialLayout> specialLayouts;
    
    @PostConstruct
    public void init() {
        componentThemes = new ArrayList<>();
        componentThemes.add(new ComponentTheme("Blue", "blue", "#3984b8"));
        componentThemes.add(new ComponentTheme("Deep-Purple", "deeppurple", "#B85CC8"));
        componentThemes.add(new ComponentTheme("Green", "green", "#37a533"));
        componentThemes.add(new ComponentTheme("Lime", "lime", "#BAD638"));
        componentThemes.add(new ComponentTheme("Orange", "orange", "#f6ac2b"));
        componentThemes.add(new ComponentTheme("Purple", "purple", "#7e8dcd"));
        componentThemes.add(new ComponentTheme("Turquoise", "turquoise", "#39b8b6"));
        componentThemes.add(new ComponentTheme("Light-Blue", "lightblue", "#63aee2"));

        flatLayouts = new ArrayList<>();
        flatLayouts.add(new FlatLayout("Default", "default", "#ffffff"));
        flatLayouts.add(new FlatLayout("Turquoise", "turquoise", "#39b8b6"));
        flatLayouts.add(new FlatLayout("Blue", "blue", "#3984b8"));
        flatLayouts.add(new FlatLayout("Deep-Purple", "deeppurple", "#B85CC8"));
        flatLayouts.add(new FlatLayout("Green", "green", "#37a533"));
        flatLayouts.add(new FlatLayout("Lime", "lime", "#BAD638"));
        flatLayouts.add(new FlatLayout("Orange", "orange", "#f6ac2b"));
        flatLayouts.add(new FlatLayout("Purple", "purple", "#7e8dcd"));
        flatLayouts.add(new FlatLayout("Red", "red", "#f28a8b"));

        specialLayouts = new ArrayList<>();
        specialLayouts.add(new SpecialLayout("Bliss", "bliss", "#360033", "#0b8793"));
        specialLayouts.add(new SpecialLayout("Cheer", "cheer", "#556270", "#ff6b6b"));
        specialLayouts.add(new SpecialLayout("Crimson", "crimson", "#642b75", "#c6426e"));
        specialLayouts.add(new SpecialLayout("Deep-Sea", "deepsea", "#2c3e50", "#4ca1af"));
        specialLayouts.add(new SpecialLayout("Disco", "disco", "#4ecdc4", "#556270"));
        specialLayouts.add(new SpecialLayout("Horizon", "horizon", "#003973", "#e5e5be"));
        specialLayouts.add(new SpecialLayout("Opa", "opa", "#3d7eaa", "#ffe47a"));
        specialLayouts.add(new SpecialLayout("Sunset", "sunset", "#e96443", "#904e95"));
        specialLayouts.add(new SpecialLayout("Smoke", "smoke", "#5b5b5b", "#5b5b5b"));
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMenuMode() {
        return this.menuMode;
    }
    
    public void setMenuMode(String menuMode) {
        this.menuMode = menuMode;
    }

    public boolean isDarkMenu() {
        return this.darkMenu;
    }

    public void setDarkMenu(boolean value) {
        this.darkMenu = value;
    }

    public String getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyleClass() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

    public List<ComponentTheme> getComponentThemes() {
        return componentThemes;
    }

    public List<FlatLayout> getFlatLayouts() {
        return flatLayouts;
    }

    public List<SpecialLayout> getSpecialLayouts() {
        return specialLayouts;
    }

    public class ComponentTheme {
        String name;
        String file;
        String color;

        public ComponentTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }

    public class FlatLayout {
        String name;
        String file;
        String color;

        public FlatLayout(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }

    public class SpecialLayout {
        String name;
        String file;
        String color1;
        String color2;

        public SpecialLayout(String name, String file, String color1, String color2) {
            this.name = name;
            this.file = file;
            this.color1 = color1;
            this.color2 = color2;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor1() {
            return color1;
        }

        public String getColor2() {
            return color2;
        }
    }
}

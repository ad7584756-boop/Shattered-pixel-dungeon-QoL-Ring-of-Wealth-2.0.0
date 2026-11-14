package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import java.util.ArrayList;

/** A divine reward that permanently adds one other hero class's base talent branch. */
public class ScrollOfDivineBranch extends Scroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_METAMORPH;
        stackable = false;
        unique = true;
    }

    @Override
    public boolean isKnown(){ return true; }

    @Override
    public void setKnown(){ }

    @Override
    public void doRead(){
        if (curUser.divineTalentClass != null){
            GLog.w(Messages.get(this, "already_chosen"));
            return;
        }
        ArrayList<HeroClass> choices = new ArrayList<>();
        for (HeroClass cls : HeroClass.values()) if (cls != curUser.heroClass) choices.add(cls);
        String[] names = new String[choices.size()];
        for (int i = 0; i < choices.size(); i++) names[i] = choices.get(i).title();
        GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(name()),
                Messages.get(this, "choose_desc"), names){
            @Override
            protected void onSelect(int index){
                if (index < 0 || index >= choices.size()) return;
                HeroClass selected = choices.get(index);
                curUser.divineTalentClass = selected;
                curUser.divineTalents = new ArrayList<>();
                Talent.initClassTalents(selected, curUser.divineTalents);
                ScrollOfDivineBranch.this.detach(ScrollOfDivineBranch.this.curUser.belongings.backpack);
                ScrollOfDivineBranch.this.identify();
                ScrollOfDivineBranch.this.readAnimation();
                ScrollOfDivineBranch.this.curUser.sprite.emitter().start(Speck.factory(Speck.STAR), 0.2f, 12);
                GLog.p(Messages.get(ScrollOfDivineBranch.class, "chosen", selected.title()));
            }
        });
    }
}

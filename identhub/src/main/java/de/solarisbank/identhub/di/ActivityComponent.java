package de.solarisbank.identhub.di;

import de.solarisbank.identhub.base.BaseActivity;
import de.solarisbank.identhub.identity.IdentityActivity;
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity;
import de.solarisbank.identhub.intro.IntroActivity;

public interface ActivityComponent {

    enum Initializer {
        ;

        public static ActivityComponent init(BaseActivity baseActivity, LibraryComponent libraryComponent) {
            return libraryComponent.plus(new ActivityModule(baseActivity));
        }
    }

    void inject(IntroActivity introActivity);

    void inject(IdentityActivity identityActivity);

    void inject(IdentitySummaryActivity identitySummaryActivity);

    FragmentComponent plus();
}

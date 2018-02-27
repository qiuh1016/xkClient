package com.cetcme.xkclient.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Fragment的显示工具类，主要是replace、switch
 */
public class FragmentUtils {


    private FragmentUtils() {

    }

    public static Fragment replaceFragment(FragmentManager fragmentManager, int container, Class<? extends Fragment> newFragment, Bundle args) {
        return replaceFragment(fragmentManager, container, newFragment, args, false);
    }

    public static Fragment replaceFragment(FragmentManager fragmentManager, int container, Fragment newFragment) {
        return replaceFragment(fragmentManager, container, newFragment, false);
    }

    public static Fragment replaceFragment(FragmentManager fragmentManager, int container, Class<? extends Fragment> newFragment, Bundle args, boolean addToBackStack) {
        Fragment fragment = null;
        // 构造新的Fragment
        try {
            fragment = newFragment.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragment != null) {
            // 设置参数
            if (args != null && !args.isEmpty()) {
                final Bundle bundle = fragment.getArguments();
                if (bundle != null) {
                    bundle.putAll(args);
                } else {
                    fragment.setArguments(args);
                }
            }
            // 替换
            return replaceFragment(fragmentManager, container, fragment, addToBackStack);
        } else {
            return null;
        }
    }

    public static Fragment replaceFragment(FragmentManager fragmentManager, int container, Fragment newFragment, boolean addToBackStack) {
        if(newFragment != null){
        	final FragmentTransaction transaction = fragmentManager.beginTransaction();
            final String tag = ((Object) newFragment).getClass().getSimpleName();

            if (newFragment != null) {
                transaction.replace(container, newFragment, tag);
            }

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
        }

        return newFragment;
    }

    public static Fragment switchFragment(FragmentManager fragmentManager, int container, Fragment currentFragment, Class<? extends Fragment> newFragment, Bundle args,boolean isSameFragment,String customTag) {
        return switchFragment(fragmentManager, container, currentFragment, newFragment, args, false,isSameFragment,customTag);
    }

    /**
     * @param fragmentManager
     * @param container
     * @param currentFragment
     * @param newFragment
     * @param args            新Fragment的参数
     * @param addToBackStack  这个操作是否加入栈中，如果要实现类似返回效果，则需要。
     * @return 新显示的Fragment
     */
//    public static Fragment switchFragment(FragmentManager fragmentManager, int container, Fragment currentFragment, Class<? extends Fragment> newFragment, Bundle args, boolean addToBackStack) {
//        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        final String tag = newFragment.getSimpleName();
//        Fragment fragment = fragmentManager.findFragmentByTag(tag);
//
//        // 如果在栈中找到相应的Fragment，则显示，否则重新生成一个
//        if (fragment != null) {
//            if (fragment != currentFragment) {
//                if (currentFragment != null) {
//                    transaction.hide(currentFragment);
//                }
//                transaction.show(fragment);
//                if (addToBackStack) {
//                    transaction.addToBackStack(null);
//                }
//                transaction.commitAllowingStateLoss();
//            } else {
//            	if(args!=null){
//            		fragment.getArguments().putAll(args);
//            	}
//            }
//
//            return fragment;
//        } else {
//            try {
//                fragment = newFragment.newInstance();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // 为新的Fragment添加参数
//        if (fragment != null) {
//            if (args != null && !args.isEmpty()) {
//                final Bundle bundle = fragment.getArguments();
//                if (bundle != null) {
//                    bundle.putAll(args);
//                } else {
//                    fragment.setArguments(args);
//                }
//            }
//        }
//
//        // 显示新的Fragment
//        if (currentFragment != null) {
//            transaction.hide(currentFragment);
//        }
//        transaction.add(container, fragment, tag);
//        if (addToBackStack) {
//            transaction.addToBackStack(null);
//        }
//        transaction.commitAllowingStateLoss();
//
//        return fragment;
//    }






    /**
     * @param fragmentManager
     * @param container
     * @param currentFragment
     * @param newFragment
     * @param args            新Fragment的参数
     * @param addToBackStack  这个操作是否加入栈中，如果要实现类似返回效果，则需要。
     * @return 新显示的Fragment
     */
    public static Fragment switchFragment(FragmentManager fragmentManager, int container, Fragment currentFragment, Class<? extends Fragment> newFragment, Bundle args, boolean addToBackStack,boolean isSameFragment,String customTag) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        String tag = null;
        if(isSameFragment){
            tag = customTag;
        }else{
            tag = newFragment.getSimpleName();
        }
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        // 如果在栈中找到相应的Fragment，则显示，否则重新生成一个
        if (fragment != null) {
            if (fragment != currentFragment) {
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                transaction.show(fragment);
                if (addToBackStack) {
                    transaction.addToBackStack(tag);
                }
                transaction.commitAllowingStateLoss();
            } else {
                if(args!=null){
                    fragment.getArguments().putAll(args);
                }
                switchFragment(transaction,container,currentFragment,fragment,tag);
            }
            return fragment;
        } else {
            try {
                fragment = newFragment.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 为新的Fragment添加参数
        if (fragment != null) {
            if (args != null && !args.isEmpty()) {
                final Bundle bundle = fragment.getArguments();
                if (bundle != null) {
                    bundle.putAll(args);
                } else {
                    fragment.setArguments(args);
                }
            }
        }

        // 显示新的Fragment
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        transaction.add(container, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();

        return fragment;
    }




    /**
     * 切换页面的重载，优化了fragment的切换
     */
    public static void switchFragment(FragmentTransaction transaction ,int container, Fragment from, Fragment to,String customTag) {
        if (from == null || to == null)
            return;
        if (!to.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            //transaction.addToBackStack(customTag);
            transaction.hide(from).add(container,to).show(to).commitAllowingStateLoss();
        } else {
            // 隐藏当前的fragment，显示下一个
            transaction.hide(from).show(to).commitAllowingStateLoss();
        }
    }


}

# Milestone 1 - Niche NHL (Unit 7)

## Table of Contents

1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview

### Description

An app that tracks niche statistics that aren't found on the official NHL app, for hockey nerds like myself. Allows users to change settings to view statistics for certain teams or to enable push notifications for statistics that occur rarely.

### App Evaluation

- **Category:** Entertainment
- **Mobile:** How uniquely mobile is the product experience?
   - Would implement the option to enable push notifications for rare statistics
   - Could try and have a feature to search for statistics only including players born within a certain radius on a map
- **Story:** How compelling is the story around this app once completed?
   - For individuals such as myself who find value in these niche statistics, I believe it would be a very valuable resource
- **Market:** How large or unique is the market for this app?
   - The NHL is the highest level of professional hockey, with millions of fans worldwide
   - I have noticed the interest in hockey statistics and data increase over the past several years
- **Habit:** How habit-forming or addictive is this app?
   - Users might use the app daily to check updated statistics
   - Difficult to have a way for users to create things
- **Scope:** How well-formed is the scope for this app?
   - The NHL API is straightforward to use, and I have experience with it in the past
   - I have already made very stripped-down versions of this app and have still enjoyed them

## Product Spec

### 1. User Features (Required and Optional)

**Required Features**

1. Create 3 screens (home, stats, settings) which the user can navigate between
2. Use the (NHL API)[https://api-web.nhle.com/] to fetch data and calculate statistics
3. Search feature where users can search through niche statistics and select one to view
4. Use Room to store data that is fetched to persist on the user's device until it is updated

**Optional Features**

1. Option to enable push notifications for certain statistics
2. Ability to view statistics between individuals from a specified radius on a map
3. Enable users to favorite certain statistics for easy access

### 2. Screen Archetypes

- Home Page
  - Displays a screen where the user can search for statistics
  - User can star/favorite certain statistics
- Statistics Page
  - User can view the statistic that they searched for
  - User can adjust settings regarding the statistic (e.g., duration, games played)
- Settings
  - User can enable post notifications
  - User can select a favorite team, highlighting instances of them in future statistics

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* Settings

**Flow Navigation** (Screen to Screen)

- Home
  - => Statistics
- Statistics
  - => Home
- Settings
  - => None

## Wireframes

<img src="https://github.com/Squekky/niche-nhl/blob/main/NicheNHLWireframeSketch.jpg" width=600>

*Note: features are likely to be added during the creation. this is not final*

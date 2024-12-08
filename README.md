# Milestone 1 - PWHL (Unit 7)

## Table of Contents

1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview

### Description

A mobile app that allows users to follow the Professional Women's Hockey League (PWHL). It contains past and future games with detailed box scores, league standings, and settings where users can choose a favorite team to follow.

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
2. Use the [NHL API](https://api-web.nhle.com/) to fetch data and calculate statistics
3. Search feature where users can search through niche statistics and select one to view
4. Use Room to store data that is fetched to persist on the user's device until it is updated

**Optional Features**

1. Option to enable push notifications for certain statistics
2. Ability to view statistics between individuals from a specified radius on a map
3. Enable users to favorite certain statistics for easy access

### 2. Screen Archetypes

- Games Page
  - Displays a page of games on a given day (default is today)
  - User move forward or backward in time and view past or future games
- Standings Page
  - User can view the current league standings, with their favorite team highlighted if they chose one
- Box Score Page
  - User can view the box score of any completed (or potentially ongoing) game
- Settings
  - User can select a favorite team to be highlighted in other areas of the app
  - User can enable notifications to be notified when games with their favorite team are about to begin

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* Standings
* Settings

**Flow Navigation** (Screen to Screen)

- Home
  - => Box Score
- Box Score
  - => Home

## Wireframes

<img src="https://github.com/Squekky/niche-nhl/blob/main/docs/milestone1/NicheNHLWireframeSketch.jpg" width=600>

<br>

# Milestone 2 - Build Sprint 1 (Unit 8)

## GitHub Project board

<img src="https://github.com/Squekky/niche-nhl/blob/main/docs/milestone2/projectBoard.png" width=600>
<img src="https://github.com/Squekky/niche-nhl/blob/main/docs/milestone2/milestones.png" width=600>

## Issue cards

This sprint:

<img src="https://github.com/Squekky/niche-nhl/blob/main/docs/milestone2/currentSprint.png" width=600>

Next sprint:

<img src="https://github.com/Squekky/niche-nhl/blob/main/docs/milestone2/nextSprint.png" width=600>

## Issues worked on this sprint

- Created 3 screens: Home, Settings, Stats
  - Home, Settings can be navigated to using the bottom navigation menu
  - Stats can be navigated to by selecting a statistic from the search menu on the Home screen
- <img src='https://github.com/Squekky/niche-nhl/blob/main/docs/milestone2/milestone2Walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

<br>

*Note: features are likely to be added during the creation. this is not final*

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
   - The PWHL currently does not have an official app for easy viewing
- **Story:** How compelling is the story around this app once completed?
   For individuals who follow hockey, and the PWHL in general, this would be a great resource considering there does not exist an official app yet
- **Market:** How large or unique is the market for this app?
   - The PWHL is the newest iteration of the highest level of women's hockey, with thousands of fans nationwide and worldwide
- **Habit:** How habit-forming or addictive is this app?
   - Users might use the app daily to check scores and standings
   - Regular notifications for a user's selected favorite team to keep them engaged
- **Scope:** How well-formed is the scope for this app?
   - The PWHL API that I found is straightforward to use, the only struggle might be gathering real-time data during ongoing games

## Product Spec

### 1. User Features (Required and Optional)

**Required Features**

1. Create 3 screens (home, standings, settings) that the user can navigate between
2. Use the [SportsRadar Global Ice Hockey API](https://developer.sportradar.com/ice-hockey/reference/global-ice-hockey-overview) to fetch data
3. Create a box score screen that can be reached through interacting with a completed (or ongoing) game, providing detailed statistics for that game
4. Utilize the SportsRadar API to provide real-time scoring updates for ongoing games
5. Use Room to store user data, such as their chosen favorite team

**Optional Features**

1. Option to enable push notifications for their favorite team
2. Expansive standings page, allowing users to select individual teams and see more details
3. Stylize the application using different themes for light and dark mode

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
- <img src='https://github.com/Squekky/niche-nhl/blob/main/docs/milestone2/milestone2Walkthrough.gif' title='Milestone 3 Walkthrough' width='' alt='Video Walkthrough' />

<br>

# Milestone 3 - Build Sprint 2 (Unit 9)

## GitHub Project board

<img src="https://github.com/Squekky/pwhl-app/blob/main/docs/milestone3/projectBoard.png" width=600>

<img src="https://github.com/Squekky/pwhl-app/blob/main/docs/milestone3/milestone3.png" width=600>

## Completed user stories

- Created a box score screen that can be reached through interacting with a completed (or ongoing) game, providing detailed statistics for that game
- Used Room to store team data for notifications
  - Decided not to use Room to store user data such as their favorite team, since using SharedPreferences was easier
- Created an option to enable push notifications for their favorite team
  - Needs to be fixed in some regards, but it does the job. The Intent to ask for permissions gets stuck on the screen until you restart the app, and it continues to notify you if you open the settings page within the 15 minutes before a game starts
- <img src='https://github.com/Squekky/niche-nhl/blob/main/docs/milestone3/milestone3Walkthrough.gif' title='Milestone 3 Walkthrough' width='' alt='Video Walkthrough' />

## App Demo Video

- Embed the YouTube/Vimeo link of your Completed Demo Day prep video

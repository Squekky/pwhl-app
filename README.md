# The PWHL App

## Table of Contents

1. [Overview](#Overview)
1. [Completed Features](#Completed-Features)

## Overview

### Description

A mobile app that allows users to follow the Professional Women's Hockey League (PWHL). It contains past and future games with detailed box scores, league standings, and settings where users can choose a favorite team to follow.

## Completed Features

- Created a box score screen that can be reached by interacting with a completed game, providing statistics for that game's scoring through each period
- Used Room to store team data for notifications
- Used SharedPreferences to save users' selected favorite team
- Created an option to enable push notifications for their favorite team
  - User is prompted for Notification permissions, followed by Alarms & Reminders permissions
  - Once both are granted, if the user has selected a favorite team, they will be notified 15 minutes before the start of every game of that team
  - Notifications can be completely disabled in the app

<img src='https://github.com/Squekky/niche-nhl/blob/main/docs/milestone3/milestone3Walkthrough.gif' title='Milestone 3 Walkthrough' width='' alt='Video Walkthrough' />

## Upcoming Features

- Bug fixes for notifications -- I believe they don't persist on system restart
- Real-time scoring updates
- More detailed box scores
- and more to come
